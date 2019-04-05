package com.kurenchuksergey.diplom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static java.lang.String.format;

@Configuration
@Profile("manager")
@EnableTransactionManagement
public class PersistenceConfiguration {
    @Value("${myapp.db.name}")
    private String databaseName;

    @Value("${myapp.db.host}")
    private String databaseHost;

    @Value("${myapp.db.port}")
    private String databasePort;


    @Bean
    @Primary
    public DataSource dataSource() {

        return DataSourceBuilder
                .create()
                .username("diplom")
                .password("diplom")
                .url(format("jdbc:postgresql://%s:%s/%s", databaseHost, databasePort, databaseName))
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    public LocalContainerEntityManagerFactoryBean
    entityManagerFactoryBean(){
        return new LocalContainerEntityManagerFactoryBean();
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactoryBean().getObject() );
        return transactionManager;
    }


//    private ServiceInstance getPostgresInstance() {
//        List<String> services = discoveryClient.getServices();
//        return discoveryClient.getInstances("postgres")
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("Unable to discover a Postgres instance"));
//    }




}
