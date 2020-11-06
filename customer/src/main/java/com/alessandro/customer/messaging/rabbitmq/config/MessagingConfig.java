package com.alessandro.customer.messaging.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    //publishing queue
    public static final String RESULT_CHECK_CUSTOMER_QUEUE_NAME = "RESULT_CHECK_CUSTOMER";

    //exchanger
    public static final String EXCHANGER_CUSTOMER_SERVICE_NAME = "EXCHANGER_CUSTOMER_SERVICE";

    //binding routing key
    public static final String ROUTINGKEY_RESULT_CHECK_CUSTOMER_NAME = "ROUTINGKEY_RESULT_CHECK_CUSTOMER";

    //subscribe queue
    public static final String CHECK_CUSTOMER_QUEUE_NAME = "CHECK_CUSTOMER";
    public static final String CREDIT_ROLLBACK_QUEUE_NAME = "CREDIT_ROLLBACK";

    @Bean
    public Queue resultCheckCustomer(){
        return new Queue(RESULT_CHECK_CUSTOMER_QUEUE_NAME);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGER_CUSTOMER_SERVICE_NAME);
    }

    @Bean
    public Binding binding(Queue resultCheckCustomer, TopicExchange exchange){
        return BindingBuilder.bind(resultCheckCustomer).to(exchange).with(ROUTINGKEY_RESULT_CHECK_CUSTOMER_NAME);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
