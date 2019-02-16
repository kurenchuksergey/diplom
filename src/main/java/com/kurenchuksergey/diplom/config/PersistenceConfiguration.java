package com.kurenchuksergey.diplom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import java.util.List;

import static java.lang.String.format;

//@Configuration
public class PersistenceConfiguration {
    @Value("${diplom.db.name}")
    private String databaseName;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public CommandLineRunner lineRunner(){
        return args -> discoveryClient.getServices().stream().forEach(System.out::println);
    }

//    @Bean
//    @Primary
//    public DataSource dataSource() {
//         ServiceInstance postgresInstance = getPostgresInstance();
//
//        return DataSourceBuilder
//                .create()
//                .username("diplom")
//                .password("diplom")
//                .url(format("jdbc:postgresql://%s:%s/%s", postgresInstance.getHost(), postgresInstance.getPort(), databaseName))
//                .driverClassName("org.postgresql.Driver")
//                .build();
//    }

    private ServiceInstance getPostgresInstance() {
        List<String> services = discoveryClient.getServices();
        return discoveryClient.getInstances("postgres")
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to discover a Postgres instance"));
    }
}
