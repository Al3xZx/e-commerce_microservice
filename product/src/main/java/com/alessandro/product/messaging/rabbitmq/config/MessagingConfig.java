package com.alessandro.product.messaging.rabbitmq.config;

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
    public static final String RESULT_CHECK_PRODUCTS_QUEUE_NAME = "RESULT_CHECK_PRODUCTS";

    //exchanger
    public static final String EXCHANGER_PRODUCT_SERVICE_NAME = "EXCHANGER_PRODUCTS_SERVICE";

    //binding routing key
    public static final String ROUTINGKEY_RESULT_CHECK_PRODUCTS_NAME = "ROUTINGKEY_RESULT_CHECK_PRODUCTS";

    //subscribe queue
    public static final String CHECK_PRODUCTS_QUEUE_NAME = "CHECK_PRODUCTS";
    //public static final String PRODUCTS_QTY_ROLLBACK_QUEUE_NAME = "PRODUCTS_QTY_ROLLBACK";

    @Bean
    public Queue resultCheckProducts(){
        return new Queue(RESULT_CHECK_PRODUCTS_QUEUE_NAME);
    }

    @Bean
    public TopicExchange exchangeProductService(){
        return new TopicExchange(EXCHANGER_PRODUCT_SERVICE_NAME);
    }

    @Bean
    public Binding binding(Queue resultCheckProducts, TopicExchange exchangeProductService){
        return BindingBuilder.bind(resultCheckProducts).to(exchangeProductService).with(ROUTINGKEY_RESULT_CHECK_PRODUCTS_NAME);
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
