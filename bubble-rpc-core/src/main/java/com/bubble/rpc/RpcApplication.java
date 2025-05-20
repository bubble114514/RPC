
package com.bubble.rpc;


import com.bubble.rpc.config.RegistryConfig;
import com.bubble.rpc.constant.RpcConstant;
import com.bubble.rpc.registry.Registry;
import com.bubble.rpc.registry.RegistryFactory;
import com.bubble.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import com.bubble.rpc.config.RpcConfig;

/**
 * RPC 框架应用
 * 相当于holder，存放了项目全局用到的变量，双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, registry = {}", registry.getClass().getName());

        //创建并注册Shutdown Hook ，JVM退出时执行
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }
    /**
     * 初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig= ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            //配置加载失败，使用默认值
            newRpcConfig=new RpcConfig();
        }
        init(newRpcConfig);
    }
    /**
     * 获取配置
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig==null){
            synchronized (RpcApplication.class){
                init();
            }
        }
        return rpcConfig;
    }
}