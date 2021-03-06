package com.kurenchuksergey.diplom.config;

import com.kurenchuksergey.diplom.config.channel.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration()
public class RabbitConfiguration {
    @Value("${rabbit.service.id}")
    private String serviceId;
    @Value("${rabbit.user}")
    private String username;
    @Value("${rabbit.pass}")
    private String password;

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ConfigurableApplicationContext context;

    public final String taskExchange = "taskExchange";


    @Bean(name = "rabbitConnect")
    public ConnectionFactory rabbitConnectionFactory() {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances.isEmpty()) {
            System.out.println("exit because rabbit not started");
            context.close();
            System.exit(-1);
            return null;
        }
        System.out.println("find" + instances.size());
        ServiceInstance serviceInstance = instances.get(0);
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        System.out.println("rabbit" + host + port);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return (ConnectionFactory) connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(@Qualifier("rabbitConnect") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(@Qualifier("rabbitConnect") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    Queue queue() {
        return new Queue(Channel.TASK_TO_WORKER.toString(), false);
    }

    @Bean
    Queue errorQueue() {
        return new Queue(Channel.TASK_ERROR.toString(), false);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(taskExchange);
    }

    @Bean
    Binding bind(@Qualifier("queue") Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }

    @Bean
    Binding bindErrorChannel(@Qualifier("errorQueue") Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }
}
