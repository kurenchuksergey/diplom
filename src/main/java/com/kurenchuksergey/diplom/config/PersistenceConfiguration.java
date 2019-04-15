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
    @Value("${app.db.name}")
    private String databaseName;

    @Value("${app.db.host}")
    private String databaseHost;

    @Value("${app.db.port}")
    private String databasePort;

    @Value("${app.db.user}")
    private String user;

    @Value("${app.db.pass}")
    private String password;



    @Bean
    @Primary
    public DataSource dataSource() {

        return DataSourceBuilder
                .create()
                .username(user)
                .password(password)
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
