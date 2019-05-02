package com.kurenchuksergey.diplom.config.worker;

import com.kurenchuksergey.diplom.config.channel.Channel;
import com.kurenchuksergey.diplom.dto.TaskDTO;
import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import com.kurenchuksergey.diplom.service.ImageService;
import com.kurenchuksergey.diplom.web.ForeignWorkerClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
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

    @Bean
    public MessageChannel amqpTaskFromManagerChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel amqpTaskErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundGateway inbound(SimpleMessageListenerContainer container,
                                      @Qualifier("amqpTaskFromManagerChannel") MessageChannel channel,
                                      @Qualifier("amqpTaskErrorChannel") MessageChannel errorChannel) {
        AmqpInboundGateway gateway = new AmqpInboundGateway(container);
        gateway.setRequestChannel(channel);
        gateway.setDefaultReplyTo("");
        gateway.setErrorChannel(errorChannel);
        return gateway;
    }


    @Bean
    @ServiceActivator(inputChannel = "amqpTaskErrorChannel")
    public AmqpOutboundEndpoint errorChannelOutbound(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setRoutingKey(Channel.TASK_ERROR.toString()); // default exchange - route to queue 'foo'
        return outbound;
    }

    @Bean
    public SimpleMessageListenerContainer containerIn(ConnectionFactory connectionFactory) {
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
                Task task = null;
                try {
                    TaskDTO taskDTO = (TaskDTO) requestMessage.getPayload();
                    byte[] image = foreignWorkerClient.getImageByTaskId(taskDTO.getId());
                    BufferedImage bufferedImage = ImageService.fromByteArray(image, taskDTO.getImageContentType());
                    BufferedImage filter = ImageService.filter(bufferedImage, taskDTO.getType());
                    BufferedImage resize = ImageService.resize(filter, Task.widthPrev, Task.heightPrev);
                    byte[] bytes = ImageService.toByteArray(filter, taskDTO.getImageContentType());
                    task = taskDTO.createTaskInst();
                    task.setState(TaskState.DONE);
                    task.setImage(bytes);
                    task.setPrevImage(ImageService.toByteArray(resize, task.getImageContentType()));
                    task = foreignWorkerClient.updateTask(task.getId(), task);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return task;
            }

        };
    }
}
