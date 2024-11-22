package com.SolarTracker.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nome da fila
    public static final String QUEUE_NAME = "solartracker.queue";

    // Nome da exchange
    public static final String EXCHANGE_NAME = "solartracker.exchange";

    // Nome da routing key
    public static final String ROUTING_KEY = "solartracker.routingkey";

    // Declaração da fila
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    // Declaração da exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Binding entre a fila, exchange e routing key
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
