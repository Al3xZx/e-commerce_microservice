package com.alessandro.customer.b_service;

import com.alessandro.customer.c_repository.CustomerRepository;
import com.alessandro.customer.d_entity.Customer;
import com.alessandro.customer.messaging.dto.MessageCustomerCheck;
import com.alessandro.customer.messaging.dto.MessageCustomerRollback;
import com.alessandro.customer.messaging.dto.ResultCustomerCheck;
import com.alessandro.customer.messaging.rabbitmq.config.MessagingConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class MessageHandler {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MessagingConfig.CHECK_CUSTOMER_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void checkCustomer(MessageCustomerCheck message) {
        Optional<Customer> oc = customerRepository.findById(message.getCustomerId());
        if(oc.isPresent()) {
            Customer c = oc.get();
            double credit = c.getCredit();
            credit = Math.floor(credit*100)/100;
            if(c.getCredit().compareTo(message.getCreditDecremet()) < 0 ) {
                rabbitTemplate.convertAndSend(
                        MessagingConfig.EXCHANGER_CUSTOMER_SERVICE_NAME,
                        MessagingConfig.ROUTINGKEY_RESULT_CHECK_CUSTOMER_NAME,
                        new ResultCustomerCheck(null, message.getOrderId(), "credito non sufficiente")
                );
            }else{
                double creditDec = credit - message.getCreditDecremet();
                creditDec =  Math.floor(creditDec*100)/100;
                c.setCredit(creditDec);
                rabbitTemplate.convertAndSend(
                        MessagingConfig.EXCHANGER_CUSTOMER_SERVICE_NAME,
                        MessagingConfig.ROUTINGKEY_RESULT_CHECK_CUSTOMER_NAME,
                        new ResultCustomerCheck(c, message.getOrderId(), "credito sufficiente")
                );
            }
        }else {
            rabbitTemplate.convertAndSend(
                    MessagingConfig.EXCHANGER_CUSTOMER_SERVICE_NAME,
                    MessagingConfig.ROUTINGKEY_RESULT_CHECK_CUSTOMER_NAME,
                    new ResultCustomerCheck(null,  message.getOrderId(), "cliente non registrato")
            );
        }
    }//checkCustomer

    @RabbitListener(queues = MessagingConfig.CREDIT_ROLLBACK_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void refundCustomer(MessageCustomerRollback message) {
        Customer c = customerRepository.findById(message.getCustomerId()).get();
        c.setCredit(c.getCredit()+message.getCreditIncrement());
    }//refundCustomer

}
