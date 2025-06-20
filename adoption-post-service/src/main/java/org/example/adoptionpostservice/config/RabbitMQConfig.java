package org.example.adoptionpostservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//SENDER CONFIGURATION
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String adottatoExchange;

    @Value("${app.rabbitmq.queue.chat-request-accepted}")
    private String chatRequestAcceptedQueue;

    @Value("${app.rabbitmq.routingkey.chat-request-accepted}")
    private String chatRequestAcceptedRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(adottatoExchange, true, false);
    }

    @Bean
    public Queue chatRequestAcceptedQueue() {
        return new Queue(chatRequestAcceptedQueue, true);
    }

    @Bean
    public Binding bindingChatRequestAccepted(Queue chatRequestAcceptedQueue, DirectExchange exchange) {
        return BindingBuilder.bind(chatRequestAcceptedQueue)
                .to(exchange)
                .with(chatRequestAcceptedRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}