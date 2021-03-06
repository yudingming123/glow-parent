package com.tm.silence.config;


import com.tm.silence.core.SqlExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yudm
 * @Date 2021/1/27 11:25
 * @Desc
 */
@Configuration
@EnableConfigurationProperties(SilenceProperty.class)
@ComponentScan({"com.tm.silence"})
public class SilenceAutoConfig {

    /*@Bean
    public SqlExecutor sqlExecutor() {
        return new SqlExecutor();
    }*/
}
