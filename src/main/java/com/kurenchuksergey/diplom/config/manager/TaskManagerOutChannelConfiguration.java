package com.kurenchuksergey.diplom.config.manager;

import com.kurenchuksergey.diplom.config.channel.Channel;
import com.kurenchuksergey.diplom.entity.Task;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
@Profile("manager")
public class TaskManagerOutChannelConfiguration {

    public final String taskExchange = "taskExchange";


    @Bean
    public MessageChannel amqpTaskToWorkerChannel() {
        return new DirectChannel();
    }

    @Profile("manager")
    @MessagingGateway(defaultRequestChannel = "amqpTaskToWorkerChannel")
    public interface TaskGateway {

        void sendToRabbit(Task data);
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpTaskToWorkerChannel")
    public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setExchangeName(taskExchange);
        outbound.setExpectReply(true);
        outbound.setRoutingKey(Channel.TASK_TO_WORKER.toString());
        return outbound;
    }



}
