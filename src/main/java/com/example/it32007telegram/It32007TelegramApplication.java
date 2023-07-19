package com.example.it32007telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
@EnableEurekaClient
@EnableFeignClients
@EnableCaching
public class It32007TelegramApplication {

    public static void main(String[] args) {
        SpringApplication.run(It32007TelegramApplication.class, args);
    }

}
