package com.EmployeeMgtSystem.AuthenticationServer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
    @Bean
    public DirectExchange userCreationExchange() {
        return new DirectExchange("user_creation_exchange");
    }

    @Bean
    public Queue userCreationRequestQueue() {
        return new Queue("employee.user.create.request.queue", false);
    }

    @Bean
    public Binding bindingRequest(Queue userCreationRequestQueue, DirectExchange userCreationExchange) {
        return BindingBuilder.bind(userCreationRequestQueue).to(userCreationExchange).with("user.create.request");
    }

    // Bind the response queue to the exchange with a routing key
    @Bean
    public Binding bindingResponse(Queue userCreationResponseQueue, DirectExchange userCreationExchange) {
        return BindingBuilder.bind(userCreationResponseQueue).to(userCreationExchange).with("user.create.response");
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
