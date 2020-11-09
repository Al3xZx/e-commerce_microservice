package com.alessandro.order_service.messaging.rabbitmq.config;

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
    public static final String CHECK_CUSTOMER_QUEUE_NAME = "CHECK_CUSTOMER";
    public static final String CREDIT_ROLLBACK_QUEUE_NAME = "CREDIT_ROLLBACK";
    public static final String CHECK_PRODUCTS_QUEUE_NAME = "CHECK_PRODUCTS";
    //public static final String PRODUCTS_QTY_ROLLBACK_QUEUE_NAME = "PRODUCTS_QTY_ROLLBACK";

    //exchanger
    public static final String EXCHANGER_ORDER_SERVICE_NAME = "EXCHANGER_ORDER_SERVICE";

    //binding routing key
    public static final String ROUTINGKEY_CHECK_CUSTOMER_NAME = "ROUTINGKEY_CHECK_CUSTOMER";
    public static final String ROUTINGKEY_CREDIT_ROLLBACK_NAME = "ROUTINGKEY_CREDIT_ROLLBACK";
    public static final String ROUTINGKEY_CHECK_PRODUCTS_NAME = "ROUTINGKEY_CHECK_PRODUCTS";
    public static final String ROUTINGKEY_PRODUCTS_QTY_ROLLBACK_NAME = "ROUTINGKEY_PRODUCTS_QTY_ROLLBACK";

    //subscribe queue
    public static final String RESULT_CHECK_CUSTOMER_QUEUE_NAME = "RESULT_CHECK_CUSTOMER";
    public static final String RESULT_CHECK_PRODUCTS_QUEUE_NAME = "RESULT_CHECK_PRODUCTS";



    @Bean
    public Queue checkCustomer(){
        return new Queue(CHECK_CUSTOMER_QUEUE_NAME);
    }

    @Bean
    public Queue creditRollback(){
        return new Queue(CREDIT_ROLLBACK_QUEUE_NAME);
    }

    @Bean
    public Queue checkProducts(){
        return new Queue(CHECK_PRODUCTS_QUEUE_NAME);
    }

//    @Bean
//    public Queue productsQtyRollback(){
//        return new Queue(PRODUCTS_QTY_ROLLBACK_QUEUE_NAME);
//    }

    @Bean
    public TopicExchange exchangeOrderService(){
        return new TopicExchange(EXCHANGER_ORDER_SERVICE_NAME);
    }

    @Bean
    public Binding binding1(Queue checkCustomer, TopicExchange exchangeOrderService){
        return BindingBuilder.bind(checkCustomer).to(exchangeOrderService).with(ROUTINGKEY_CHECK_CUSTOMER_NAME);
    }

    @Bean
    public Binding binding2(Queue creditRollback, TopicExchange exchange){
        return BindingBuilder.bind(creditRollback).to(exchange).with(ROUTINGKEY_CREDIT_ROLLBACK_NAME);
    }

    @Bean
    public Binding binding3(Queue checkProducts, TopicExchange exchange){
        return BindingBuilder.bind(checkProducts).to(exchange).with(ROUTINGKEY_CHECK_PRODUCTS_NAME);
    }

//    @Bean
//    public Binding binding4(Queue productsQtyRollback, TopicExchange exchange){
//        return BindingBuilder.bind(productsQtyRollback).to(exchange).with(ROUTINGKEY_PRODUCTS_QTY_ROLLBACK_NAME);
//    }

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
