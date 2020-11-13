package com.alessandro.product.messaging.pubsub.conf;


import com.alessandro.product.messaging.dto.ProductsOL;
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
    public MessageChannel resultCheckProductsOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "resultCheckProductsOutputChannel")
    public MessageHandler messageSender1(PubSubTemplate pubSubTemplate) {
        return new PubSubMessageHandler(pubSubTemplate, PubSubConf.RESULT_CHECK_PRODUCTS_TOPIC);
    }

    @MessagingGateway(defaultRequestChannel = "resultCheckProductsOutputChannel")
    public interface ResultCheckProductsOutboundGateway  {
        void sendResultCheckProductsToPubSub(ProductsOL productsOL);
    }




}
