package com.alessandro.order_service.messaging.pubsub.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.support.converter.JacksonPubSubMessageConverter;
import org.springframework.cloud.gcp.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConf {

    @Autowired
    PubSubAdmin pubSubAdmin;

    //publishing
    public static final String CHECK_CUSTOMER_TOPIC = "CHECK_CUSTOMER_TP";
    public static final String CREDIT_ROLLBACK_TOPIC = "CREDIT_ROLLBACK_TP";
    public static final String CHECK_PRODUCTS_TOPIC = "CHECK_PRODUCTS_TP";

    //subscription
    public static final String RESULT_CHECK_CUSTOMER_SUBSCRIPTION = "RESULT_CHECK_CUSTOMER";
    public static final String RESULT_CHECK_CUSTOMER_TOPIC = "RESULT_CHECK_CUSTOMER_TP";

    public static final String RESULT_CHECK_PRODUCTS_SUBSCRIPTION = "RESULT_CHECK_PRODUCTS";
    public static final String RESULT_CHECK_PRODUCTS_TOPIC = "RESULT_CHECK_PRODUCTS_TP";


    @Bean
    public void newTopic() {
        if(pubSubAdmin.getTopic(CHECK_CUSTOMER_TOPIC) == null)
            pubSubAdmin.createTopic(CHECK_CUSTOMER_TOPIC);
    }

    @Bean
    public void newTopic2() {
        if(pubSubAdmin.getTopic(CREDIT_ROLLBACK_TOPIC) == null)
            pubSubAdmin.createTopic(CREDIT_ROLLBACK_TOPIC);
    }

    @Bean
    public void newTopic3() {
        if(pubSubAdmin.getTopic(CHECK_PRODUCTS_TOPIC) == null)
            pubSubAdmin.createTopic(CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public void newTopic4() {
        if(pubSubAdmin.getTopic(RESULT_CHECK_CUSTOMER_TOPIC) == null)
            pubSubAdmin.createTopic(RESULT_CHECK_CUSTOMER_TOPIC);
    }

    @Bean
    public void newTopic5() {
        if(pubSubAdmin.getTopic(RESULT_CHECK_PRODUCTS_TOPIC) == null)
            pubSubAdmin.createTopic(RESULT_CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public void newSubscription() {
        if(pubSubAdmin.getSubscription(RESULT_CHECK_CUSTOMER_SUBSCRIPTION) == null)
            pubSubAdmin.createSubscription(RESULT_CHECK_CUSTOMER_SUBSCRIPTION, RESULT_CHECK_CUSTOMER_TOPIC);
    }

    @Bean
    public void newSubscription2() {
        if(pubSubAdmin.getSubscription(RESULT_CHECK_PRODUCTS_SUBSCRIPTION) == null)
            pubSubAdmin.createSubscription(RESULT_CHECK_PRODUCTS_SUBSCRIPTION, RESULT_CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public PubSubMessageConverter pubSubMessageConverter() {
        return new JacksonPubSubMessageConverter(new ObjectMapper());
    }

}
