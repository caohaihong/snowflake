package com.chh.www;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 12:28 2019-03-22
 * @Modified:
 */
@SpringBootApplication
@EnableConfigurationProperties
public class SnowflakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SnowflakeApplication.class, args);
    }
}
