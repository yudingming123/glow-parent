package com.jimei.glow.server.core;

import lombok.Data;

/**
 * @Author yudm
 * @Date 2020/12/12 14:38
 * @Desc glow-server的配置信息类
 */
@Data
public class Property {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String type;

}
