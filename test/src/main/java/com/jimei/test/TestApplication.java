package com.jimei.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author yudm
 * @Date 2021/5/30 19:05
 * @Desc
 */
@MapperScan("com.jimei.test.dao.mapper")
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
