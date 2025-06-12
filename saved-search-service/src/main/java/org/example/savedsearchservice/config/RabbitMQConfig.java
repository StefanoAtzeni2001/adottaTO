package org.example.savedsearchservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//RECIVER CONFIGURATION
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String adottatoExchange;

    @Value("${app.rabbitmq.queue}")
    private String savedSearchQueue;

    @Value("${app.rabbitmq.routingkey.new-post}")
    private String newPostRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(adottatoExchange, true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue(savedSearchQueue, true);
    }
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(newPostRoutingKey);
    }

    //mapping object <--> json
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}