package com.jimei.glow.client.config;

import lombok.Data;


/**
 * @Author yudm
 * @Date 2020/12/12 14:38
 * @Desc glow-client的配置信息类
 */
@Data
public class HttpProperty {
    /**
     * 同路由的并发数
     */
    private int maxConnectPerRoute;
    /**
     * 客户端和服务器建立连接超时，默认2s
     */
    private long connectTimeout = 2 * 1000;
    /**
     * 指客户端从服务器读取数据包的间隔超时时间,不是总读取时间，默认10s
     */
    private long socketTimeout = 10 * 1000;

    private String charset = "UTF-8";
    /**
     * 重试次数,默认2次
     */
    private int retryTimes = 2;
    /**
     * 从连接池获取连接的超时时间,不宜过长,单位ms
     */
    private long connectionRequestTimout = 200;

    /**
     * 长连接保持时间,单位ms
     */
    private int keepAliveTime = 60 * 1000;

}
