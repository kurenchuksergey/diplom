package com.kurenchuksergey.diplom.config.worker;

import com.kurenchuksergey.diplom.config.channel.Channel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Profile("worker")
@Configuration
public class DoneTaskWorkerChannelConfiguration {

    @Bean
    @ServiceActivator(inputChannel = "amqpOutboundChannel")
    public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setExpectReply(true);
        outbound.setExchangeName("taskExchange");
        outbound.setRoutingKey(Channel.TASK_TO_MANAGER.toString()); // default exchange - route to queue 'foo'
        return outbound;
    }

    @Bean
    public MessageChannel amqpOutboundChannel() {
        return new DirectChannel();
    }

    @Profile("worker")
    @MessagingGateway(defaultRequestChannel = "amqpOutboundChannel")
    public interface TaskGateway {

        String sendToRabbit(String data);

    }

}
