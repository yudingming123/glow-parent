package com.jimei.silence.config;

import lombok.Data;

import java.util.Set;

/**
 * @Author yudm
 * @Date 2020/12/12 14:38
 * @Desc glow-server的配置信息类
 */
@Data
public class Property {
    private String username;
    private String password;
    private String driverClassName;
    private String mapperScan;
    private Set<String> jdbcUrls;
}
