package com.bubble.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class EtcdRegistry implements Registry {
    private Client client;
    private KV kvClient;
    /**
     * 本机注册节点key集合（用于维护续期）
     */
    private final Set<String> localRegistryKeySet = new HashSet<>();
    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听的key集合
     */
    private final Set<String> watchKeySet = new ConcurrentHashSet<>();

    private static volatile boolean schedulerStarted = false; // 添加调度器状态标志

    /**
     * 心跳
     *  即使Etcd 注册中心的数据出现了丢失，通过心跳检测机制也会重新注册节点信息。
     */
    @Override
    public void heartBeat() {
        // 确保调度器只启动一次
        if (!schedulerStarted) {
            synchronized (EtcdRegistry.class) {
                if (!schedulerStarted) {
                    //10秒续签一次
                    CronUtil.schedule("0/10 * * * * *", (Task) () -> {
                        //遍历本节点的所有key
                        for (String key : localRegistryKeySet) {
                            try {
                                List<KeyValue> keyValues = kvClient.get(
                                                ByteSequence.from(key, StandardCharsets.UTF_8))
                                        .get()
                                        .getKvs();
                                if (CollUtil.isEmpty(keyValues)) {
                                    continue;
                                }
                                KeyValue keyValue = keyValues.get(0);
                                ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(
                                        keyValue.getValue().toString(StandardCharsets.UTF_8),
                                        ServiceMetaInfo.class);
                                register(serviceMetaInfo);
                            } catch (Exception e) {
                                System.err.println(key + "续签失败: " + e.getMessage());
                            }
                        }
                    });
                    //支持秒级别定时任务
                    CronUtil.setMatchSecond(true);
                    CronUtil.start();
                    schedulerStarted = true;
                }
            }
        }
    }


    /**
     * 监听（消费端）
     * @param key
     */
    @Override
    public void watch(String key) {
        Watch watchClient = client.getWatchClient();
        //之前未被监听，开启监听
        boolean newWatch=watchKeySet.add(key);
        if(newWatch){//开启监听
            watchClient.watch(ByteSequence.from(key, StandardCharsets.UTF_8),  response -> {
                for(WatchEvent event : response.getEvents()){
                    switch (event.getEventType()){
                        //key删除时触发
                        case DELETE :
                            //清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT :
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .build();
        kvClient = client.getKVClient();

        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();
        //创建30s的租约
        long leaseId = leaseClient.grant(30L).get().getID();

        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对和租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        //添加节点信息到本地缓存
        localRegistryKeySet.add(registryKey);
    }


    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceKey();
        kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8));
        //从本地缓存中移除
        localRegistryKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从缓存获取服务
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if (cacheServiceMetaInfoList!=null){
            return cacheServiceMetaInfoList;
        }
        //前缀搜索，结尾记得加‘/’
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(kv -> {
                String key = kv.getKey().toString(StandardCharsets.UTF_8);
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                //监听key的变化
                watch(key);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());

            //写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }


    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //下线节点
        //遍历本节点所有key
        for (String key : localRegistryKeySet){
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(key + "节点下线失败",e);
            }
        }
        //释放资源
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }


//    //本地注册中心测试
//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        // create client using endpoints
//        Client client = Client.builder().endpoints("http://localhost:2379")
//                .build();
//
//        KV kvClient = client.getKVClient();
//        ByteSequence key = ByteSequence.from("test_key".getBytes());
//        ByteSequence value = ByteSequence.from("test_value".getBytes());
//
//        // put the key-value
//        kvClient.put(key, value).get();
//
//        // get the CompletableFuture
//        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
//
//        // get the value from CompletableFuture
//        GetResponse response = getFuture.get();
//
//        // delete the key
//        kvClient.delete(key).get();
//    }
}

