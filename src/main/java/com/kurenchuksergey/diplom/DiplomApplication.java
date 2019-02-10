package com.kurenchuksergey.diplom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@RefreshScope
@EnableFeignClients
@EnableDiscoveryClient
public class DiplomApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiplomApplication.class, args);
    }

}

