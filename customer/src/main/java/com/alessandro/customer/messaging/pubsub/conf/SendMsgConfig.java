package com.alessandro.customer.messaging.pubsub.conf;


import com.alessandro.customer.messaging.dto.ResultCustomerCheck;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class SendMsgConfig {

    @Bean
    public MessageChannel resultCheckCustomerOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "resultCheckCustomerOutputChannel")
    public MessageHandler messageSender1(PubSubTemplate pubSubTemplate) {
        return new PubSubMessageHandler(pubSubTemplate, PubSubConf.RESULT_CHECK_CUSTOMER_TOPIC);
    }

    @MessagingGateway(defaultRequestChannel = "resultCheckCustomerOutputChannel")
    public interface ResultCheckCustomerOutboundGateway  {
        void sendResultCheckCustomerToPubSub(ResultCustomerCheck customerCheck);
    }

}
