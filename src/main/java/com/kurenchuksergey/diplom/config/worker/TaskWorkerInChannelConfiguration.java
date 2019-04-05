package com.kurenchuksergey.diplom.config.worker;

import com.kurenchuksergey.diplom.config.channel.Channel;
import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import com.kurenchuksergey.diplom.service.ImageService;
import com.kurenchuksergey.diplom.web.ForeignWorkerClient;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Configuration
@Profile("worker")
public class TaskWorkerInChannelConfiguration {

    @Autowired
    private ForeignWorkerClient foreignWorkerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public MessageChannel amqpTaskFromManagerChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundGateway inbound(SimpleMessageListenerContainer container,
                                      @Qualifier("amqpTaskFromManagerChannel") MessageChannel channel) {
        AmqpInboundGateway gateway = new AmqpInboundGateway(container);
        gateway.setRequestChannel(channel);
        gateway.setDefaultReplyTo("");
        return gateway;
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(Channel.TASK_TO_WORKER.toString());
        container.setConcurrentConsumers(2);
        return container;
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpTaskFromManagerChannel")
    public MessageHandler handler() {
        return new AbstractReplyProducingMessageHandler() {

            @Override
            protected Object handleRequestMessage(Message<?> requestMessage) {
                Task task = (Task)requestMessage.getPayload();
                byte[] image = task.getImage();
                try {
                    BufferedImage bufferedImage = ImageService.fromByteArray(image, task.getImageContentType());
                    BufferedImage filter = ImageService.filter(bufferedImage);
                    BufferedImage resize = ImageService.resize(filter, task.getWidthPrev(), task.getHeightPrev());
                    byte[] bytes = ImageService.toByteArray(filter, task.getImageContentType());
                    task.setState(TaskState.DONE);
                    task.setImage(bytes);
                    task.setPrevImage(ImageService.toByteArray(resize, task.getImageContentType()));
                    foreignWorkerClient.updateTask(task.getId(), task);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

//                taskGateway.sendToRabbit(task);
                return null;
            }

        };
    }
}
