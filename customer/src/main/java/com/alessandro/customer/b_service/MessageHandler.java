package com.alessandro.customer.b_service;

import com.alessandro.customer.c_repository.CustomerRepository;
import com.alessandro.customer.d_entity.Customer;
import com.alessandro.customer.messaging.dto.MessageCustomerCheck;
import com.alessandro.customer.messaging.dto.MessageCustomerRollback;
import com.alessandro.customer.messaging.dto.ResultCustomerCheck;
import com.alessandro.customer.messaging.pubsub.conf.PubSubConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class MessageHandler implements ApplicationListener<ApplicationReadyEvent> {

    private Log log = LogFactory.getLog(MessageHandler.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PubSubTemplate pubSubTemplate;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        checkCustomerListener();
        refundCustomerMessage();
    }


    public void checkCustomerListener(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.CHECK_CUSTOMER_SUBSCRIPTION,
                (message) ->{
                    MessageCustomerCheck m = message.getPayload();
                    checkCustomer(m);
                    message.ack();
                },
                MessageCustomerCheck.class
        );
    }

    @Transactional(readOnly = false)
    public void checkCustomer(MessageCustomerCheck message) {
        Optional<Customer> oc = customerRepository.findById(message.getCustomerId());
        log.info("ricevuto messaggio di verifica del cliente " + message.getCustomerId());
//        System.out.println("ricevuto messaggio di verifica del cliente " + message.getCustomerId());
        if(oc.isPresent()) {
            Customer c = oc.get();
            double credit = c.getCredit();
            credit = Math.floor(credit*100)/100;
            if(c.getCredit().compareTo(message.getCreditDecremet()) < 0 ) {
                pubSubTemplate.publish(
                        PubSubConf.RESULT_CHECK_CUSTOMER_TOPIC,
                        new ResultCustomerCheck(null, message.getOrderId(), "credito non sufficiente")
                );
            }else{
                double creditDec = credit - message.getCreditDecremet();
                creditDec =  Math.floor(creditDec*100)/100;
                c.setCredit(creditDec);
                customerRepository.saveAndFlush(c);
                pubSubTemplate.publish(
                        PubSubConf.RESULT_CHECK_CUSTOMER_TOPIC,
                        new ResultCustomerCheck(c, message.getOrderId(), "credito sufficiente")
                );
            }
        }else {
            pubSubTemplate.publish(
                    PubSubConf.RESULT_CHECK_CUSTOMER_TOPIC,
                    new ResultCustomerCheck(null,  message.getOrderId(), "cliente non registrato")
            );
        }
    }//checkCustomer


    public void refundCustomerMessage(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.CREDIT_ROLLBACK_SUBSCRIPTION,
                (message) ->{
                    MessageCustomerRollback m = message.getPayload();
                    refundCustomer(m);
                    message.ack();

                },
                MessageCustomerRollback.class
        );
    }

    @Transactional(readOnly = false)
    public void refundCustomer(MessageCustomerRollback message) {
        log.info("ricevuto messaggio di rimborso del cliente " + message.getCustomerId());
//        System.out.println("ricevuto messaggio di rimborso del cliente " + message.getCustomerId());
        Customer c = customerRepository.findById(message.getCustomerId()).get();
        double creditInc = c.getCredit()+message.getCreditIncrement();
        creditInc = Math.floor(creditInc*100)/100;
        c.setCredit(creditInc);
        customerRepository.saveAndFlush(c);
    }//refundCustomer

}
