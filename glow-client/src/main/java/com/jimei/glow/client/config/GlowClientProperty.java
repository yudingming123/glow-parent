package com.jimei.glow.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author yudm
 * @Date 2020/12/12 11:00
 * @Desc glow-client的配置信息类
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource.glow-client")
public class GlowClientProperty {
    /**
     * glow服务端的地址，配置了这一项说明使用远程模式，后面的配置才会生效,同时也会使原始的DataSource配置失效，因为这些配置根本不会用到，
     * 否则使用本地模式及原始mybatis模式
     */
    private String serverUrl;
    /**
     * 同路由的并发数
     */
    private int maxConnectPerRoute;
    /**
     * 客户端和服务器建立连接超时，默认2s
     */
    private int connectTimeout = 2 * 1000;
    /**
     * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间，默认10s
     */
    private int socketTimeout = 10 * 1000;

    /**
     * 字符集
     */
    private String charset = "UTF-8";
    /**
     * 重试次数,默认2次
     */
    private int retryTimes = 2;
    /**
     * 从连接池获取连接的超时时间,不宜过长,单位ms
     */
    private int connectionRequestTimout = 200;

    /**
     * 长连接保持时间,单位ms
     */
    private int keepAliveTime = 60 * 1000;

    private HttpProperty httpPool;
}
