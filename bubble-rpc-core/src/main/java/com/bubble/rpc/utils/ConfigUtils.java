package com.bubble.rpc.utils;

import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {
    /**
     * 加载配置对象
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass,String prefix){
        return loadConfig(tClass,prefix,"");
    }

    /**
     * 加载配置对象，带环境参数
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass,String prefix,String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(environment != null && !environment.isEmpty()){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props=new Props(configFileBuilder.toString());
        return props.toBean(tClass,prefix);
    }
}
