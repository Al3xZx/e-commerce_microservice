package com.alessandro.customer.messaging.pubsub.conf;

import com.alessandro.customer.messaging.dto.MessageCustomerCheck;
import com.alessandro.customer.messaging.dto.MessageCustomerRollback;
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
    public MessageChannel checkCustomerInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel creditRollbackInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter1(
            @Qualifier("checkCustomerInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, PubSubConf.CHECK_CUSTOMER_SUBSCRIPTION);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(MessageCustomerCheck.class);
        return adapter;
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter2(
            @Qualifier("creditRollbackInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, PubSubConf.CREDIT_ROLLBACK_SUBSCRIPTION);
        adapter.setOutputChannel(inputChannel);
//        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(MessageCustomerRollback.class);
        return adapter;
    }




}
