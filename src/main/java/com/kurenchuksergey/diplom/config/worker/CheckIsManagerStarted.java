package com.kurenchuksergey.diplom.config.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("worker")
public class CheckIsManagerStarted {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ConfigurableApplicationContext context;

    private final String managerServiceId = "manager";

    @Bean(name = "check")
    public CommandLineRunner checkIsManagerStarted() {
        return t -> {
            if (discoveryClient.getInstances(managerServiceId).isEmpty()) {
                System.out.println("exit because " + managerServiceId + " not started");
                context.close();
                System.exit(-1);
            }
        };
    }
}