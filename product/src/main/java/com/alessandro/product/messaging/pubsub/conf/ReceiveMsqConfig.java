package com.alessandro.product.messaging.pubsub.conf;

import com.alessandro.product.messaging.dto.ProductsOL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ReceiveMsqConfig {

    @Bean
    public MessageChannel checkProductInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter2(
            @Qualifier("checkProductInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, PubSubConf.CHECK_PRODUCTS_SUBSCRIPTION);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(ProductsOL.class);
        return adapter;
    }
}
