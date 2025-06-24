package it.unito.emailservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

//RECEIVER CONFIGURATION
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue.chat-notification}")
    private String chatQueue;

    @Value("${app.rabbitmq.queue.savedsearch-match}")
    private String savedSearchQueue;

    @Value("${app.rabbitmq.routingkey.chat-notification}")
    private String chatRoutingKey;

    @Value("${app.rabbitmq.routingkey.savedsearch-match}")
    private String savedSearchRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // === CHAT QUEUE CONFIGURATION ===
    @Bean
    public Queue chatQueue() {
        return new Queue(chatQueue, true);
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, DirectExchange exchange) {
        return BindingBuilder.bind(chatQueue).to(exchange).with(chatRoutingKey);
    }

    @Bean
    public Queue savedSearchQueue() {
        return new Queue(savedSearchQueue, true);
    }

    @Bean
    public Binding savedSearchBinding(Queue savedSearchQueue, DirectExchange exchange) {
        return BindingBuilder.bind(savedSearchQueue).to(exchange).with(savedSearchRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
