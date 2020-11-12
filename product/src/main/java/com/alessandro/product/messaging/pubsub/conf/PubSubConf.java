package com.alessandro.product.messaging.pubsub.conf;

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
    public static final String RESULT_CHECK_PRODUCTS_TOPIC = "RESULT_CHECK_PRODUCTS_TP";

    //subscription
    public static final String CHECK_PRODUCTS_SUBSCRIPTION = "CHECK_PRODUCTS";
    public static final String CHECK_PRODUCTS_TOPIC = "CHECK_PRODUCTS_TP";

    @Bean
    public void newTopic() {
        if(pubSubAdmin.getTopic(RESULT_CHECK_PRODUCTS_TOPIC) == null)
            pubSubAdmin.createTopic(RESULT_CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public void newTopic2() {
        if(pubSubAdmin.getTopic(CHECK_PRODUCTS_TOPIC) == null)
            pubSubAdmin.createTopic(CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public void newSubscription() {
        if(pubSubAdmin.getSubscription(CHECK_PRODUCTS_SUBSCRIPTION) == null)
            pubSubAdmin.createSubscription(CHECK_PRODUCTS_SUBSCRIPTION, CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    public PubSubMessageConverter pubSubMessageConverter() {
        return new JacksonPubSubMessageConverter(new ObjectMapper());
    }
}
