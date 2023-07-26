package com.example.partypal;

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
public class TelegramApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramApplication.class, args);
    }

}
