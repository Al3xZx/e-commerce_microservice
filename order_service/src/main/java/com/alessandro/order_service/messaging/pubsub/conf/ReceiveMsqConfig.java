package com.alessandro.order_service.messaging.pubsub.conf;

import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.dto.ResultCustomerCheck;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ReceiveMsqConfig {

    @Bean
    public MessageChannel resultCheckCustomerInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel resultCheckProductInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter1(
            @Qualifier("resultCheckCustomerInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, PubSubConf.RESULT_CHECK_CUSTOMER_SUBSCRIPTION);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(ResultCustomerCheck.class);
        return adapter;
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter2(
            @Qualifier("resultCheckProductInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, PubSubConf.RESULT_CHECK_PRODUCTS_SUBSCRIPTION);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(ProductsOL.class);
        return adapter;
    }

}
