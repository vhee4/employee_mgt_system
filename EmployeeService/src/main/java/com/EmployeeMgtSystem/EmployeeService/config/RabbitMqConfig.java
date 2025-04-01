package com.EmployeeMgtSystem.EmployeeService.config;

import com.EmployeeMgtSystem.EmployeeService.dto.request.CreateUserRequest;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
    @Bean
    public DirectExchange userCreationExchange() {
        return new DirectExchange("user_creation_exchange");
    }

    @Bean
    public Queue userCreationResponseQueue() {
        return new Queue("employee.user.create.response.queue", false);
    }

    @Bean
    public Binding bindingResponse(Queue userCreationResponseQueue, DirectExchange userCreationExchange) {
        return BindingBuilder.bind(userCreationResponseQueue).to(userCreationExchange).with("user.create.response");
    }
    @Bean
    public Binding bindingRequest(Queue userCreationRequestQueue, DirectExchange userCreationExchange) {
        return BindingBuilder.bind(userCreationRequestQueue).to(userCreationExchange).with("user.create.request");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        //        rabbitTemplate.setMessageConverter(converter);
//        rabbitTemplate.setReplyTimeout(10000); // Set reply timeout to 10 seconds
//        rabbitTemplate.setReceiveTimeout(10000);
        return new RabbitTemplate(connectionFactory);
    }

}
