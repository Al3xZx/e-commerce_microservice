package com.alessandro.customer.b_service;

import com.alessandro.customer.c_repository.CustomerRepository;
import com.alessandro.customer.d_entity.Customer;
import com.alessandro.customer.messaging.dto.MessageCustomerCheck;
import com.alessandro.customer.messaging.dto.MessageCustomerRollback;
import com.alessandro.customer.messaging.dto.ResultCustomerCheck;
import com.alessandro.customer.messaging.pubsub.conf.PubSubConf;
import com.alessandro.customer.messaging.pubsub.conf.SendMsgConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class MessageHandler {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SendMsgConfig.ResultCheckCustomerOutboundGateway resultCheckCustomerOutboundGateway;


    @ServiceActivator(inputChannel = "checkCustomerInputChannel")
    public void checkCustomerListener(MessageCustomerCheck payload){
        checkCustomer(payload);
    }

    @Transactional(readOnly = false)
    public void checkCustomer(MessageCustomerCheck message) {
        Optional<Customer> oc = customerRepository.findById(message.getCustomerId());
        System.out.println("ricevuto messaggio di verifica del cliente " + message.getCustomerId());
        if(oc.isPresent()) {
            Customer c = oc.get();
            double credit = c.getCredit();
            credit = Math.floor(credit*100)/100;
            if(c.getCredit().compareTo(message.getCreditDecremet()) < 0 ) {
                resultCheckCustomerOutboundGateway.sendResultCheckCustomerToPubSub(
                        new ResultCustomerCheck(null, message.getOrderId(), "credito non sufficiente")
                );
            }else{
                double creditDec = credit - message.getCreditDecremet();
                creditDec =  Math.floor(creditDec*100)/100;
                c.setCredit(creditDec);
                resultCheckCustomerOutboundGateway.sendResultCheckCustomerToPubSub(
                        new ResultCustomerCheck(c, message.getOrderId(), "credito sufficiente")
                );
                customerRepository.saveAndFlush(c);
            }
        }else {
            resultCheckCustomerOutboundGateway.sendResultCheckCustomerToPubSub(
                    new ResultCustomerCheck(null,  message.getOrderId(), "cliente non registrato")
            );
        }
    }//checkCustomer

    @ServiceActivator(inputChannel = "creditRollbackInputChannel")
    public void refoundCustomerMessage(MessageCustomerRollback payload){
        refundCustomer(payload);
    }

    @Transactional(readOnly = false)
    public void refundCustomer(MessageCustomerRollback message) {
        System.out.println("ricevuto messaggio di rimborso del cliente " + message.getCustomerId());
        Customer c = customerRepository.findById(message.getCustomerId()).get();
        c.setCredit(c.getCredit()+message.getCreditIncrement());
        customerRepository.saveAndFlush(c);
    }//refundCustomer

}
