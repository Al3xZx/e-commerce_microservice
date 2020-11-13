package com.alessandro.order_service.messaging.pubsub.conf;


import com.alessandro.order_service.messaging.dto.MessageCustomerCheck;
import com.alessandro.order_service.messaging.dto.MessageCustomerRollback;
import com.alessandro.order_service.messaging.dto.ProductsOL;
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
    public MessageChannel checkCustomerOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel checkProductsOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel creditRollbackOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "checkCustomerOutputChannel")
    public MessageHandler messageSender1(PubSubTemplate pubSubTemplate) {
        return new PubSubMessageHandler(pubSubTemplate, PubSubConf.CHECK_CUSTOMER_TOPIC);
    }

    @Bean
    @ServiceActivator(inputChannel = "checkProductsOutputChannel")
    public MessageHandler messageSender2(PubSubTemplate pubSubTemplate) {
        return new PubSubMessageHandler(pubSubTemplate, PubSubConf.CHECK_PRODUCTS_TOPIC);
    }

    @Bean
    @ServiceActivator(inputChannel = "creditRollbackOutputChannel")
    public MessageHandler messageSender3(PubSubTemplate pubSubTemplate) {
        return new PubSubMessageHandler(pubSubTemplate, PubSubConf.CREDIT_ROLLBACK_TOPIC);
    }

    @MessagingGateway(defaultRequestChannel = "checkCustomerOutputChannel")
    public interface CheckCustomerOutboundGateway  {
        void sendCheckCustomerToPubSub(MessageCustomerCheck customerCheck);
    }

    @MessagingGateway(defaultRequestChannel = "checkProductsOutputChannel")
    public interface CheckProductsOutboundGateway  {
        void sendCheckProductsToPubSub(ProductsOL productsOL);
    }

    @MessagingGateway(defaultRequestChannel = "creditRollbackOutputChannel")
    public interface CreditRollbackOutboundGateway  {
        void sendCreditRollbackToPubSub(MessageCustomerRollback customerRollback);
    }



}
