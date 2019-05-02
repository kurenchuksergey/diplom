package com.kurenchuksergey.diplom.config.manager;

import com.kurenchuksergey.diplom.config.channel.Channel;
import com.kurenchuksergey.diplom.dto.TaskDTO;
import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskState;
import com.kurenchuksergey.diplom.repository.TaskRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Objects;

@Configuration
@Profile("manager")
public class TaskManagerOutChannelConfiguration {

    public final String taskExchange = "taskExchange";
    private Logger logger = LoggerFactory.getLogger(TaskManagerOutChannelConfiguration.class);


    @Autowired
    private TaskRepository taskRepository;


    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaExecutor jpaExecutor() {
        JpaExecutor executor = new JpaExecutor(this.entityManagerFactory);
        executor.setJpaQuery("from Task t where t.state = 'WAIT' or t.state = 'FAIL'");
        return executor;
    }


    @Bean
    @InboundChannelAdapter(channel = "jpaInputChannel",
            poller = @Poller(fixedDelay = "5000"))
    public MessageSource<?> jpaInbound() {
        return new JpaPollingChannelAdapter(jpaExecutor());
    }

    @ServiceActivator(inputChannel = "jpaInputChannel")
    @Bean
    public MessageHandler retrievingJpaGateway(TaskGateway taskGateway) {
        return mess -> {
            List<Task> payload = (List<Task>) mess.getPayload();
            payload.stream().forEach(task ->
            {
                task.setState(TaskState.PROGRESS);
                taskRepository.save(task);
                TaskDTO dto = new TaskDTO(task);
                taskGateway.sendToRabbit(dto);
            });

        };
    }

    @Bean
    public MessageChannel amqpTaskErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public SimpleMessageListenerContainer containerError(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(Channel.TASK_ERROR.toString());
        return container;
    }


    @Bean
    public AmqpInboundGateway inboundError(SimpleMessageListenerContainer container,
                                           @Qualifier("amqpTaskErrorChannel") MessageChannel channel) {
        AmqpInboundGateway gateway = new AmqpInboundGateway(container);
        gateway.setRequestChannel(channel);
        return gateway;
    }

    @ServiceActivator(inputChannel = "amqpTaskErrorChannel")
    @Bean
    public MessageHandler errorChannelHandler() {
        return mess -> {
            MessageHandlingException exception = ((MessageHandlingException) mess.getPayload());
            Throwable cause = exception.getCause();
            TaskDTO dto = (TaskDTO) Objects.requireNonNull(exception.getFailedMessage()).getPayload();

            Task task = taskRepository.findById(dto.getId()).get();
            Hibernate.initialize(task.getState());
            if (task.getState().equals(TaskState.PROGRESS)) {
                task.setState(TaskState.FAIL);
                logger.error("Task fail id" + task.getId() + " cause" + cause.getMessage());
                taskRepository.save(task);
            }

        };
    }

    @Bean
    public MessageChannel amqpTaskToWorkerChannel() {
        return new DirectChannel();
    }

    @Profile("manager")
    @MessagingGateway(defaultRequestChannel = "amqpTaskToWorkerChannel")
    public interface TaskGateway {

        void sendToRabbit(TaskDTO data);
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpTaskToWorkerChannel")
    public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setExchangeName(taskExchange);
        outbound.setExpectReply(true);
        outbound.setRoutingKey(Channel.TASK_TO_WORKER.toString());
        outbound.setSendTimeout(2000);
        return outbound;
    }


}
