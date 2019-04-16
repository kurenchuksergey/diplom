package com.kurenchuksergey.diplom.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class RabbitConfiguration {
    @Value("${rabbit.service.id}")
    private String serviceId;
    @Value("${rabbit.user}")
    private String username;
    @Value("${rabbit.pass}")
    private String password;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Primary
    public ConnectionFactory rabbitConnectionFactory() {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = instances.get(0);
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return (ConnectionFactory) connectionFactory;
    }
}
