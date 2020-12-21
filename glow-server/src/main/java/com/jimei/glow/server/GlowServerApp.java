package com.jimei.glow.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Author yudm
 * @Date 2020/12/21 11:06
 * @Desc
 */
@EnableConfigurationProperties
@SpringBootApplication
public class GlowServerApp {
    public static void main(String[] args) {
        SpringApplication.run(GlowServerApp.class, args);
    }

}
