package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.messaging.dto.Customer;
import com.alessandro.order_service.messaging.dto.MessageCustomerRollback;
import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.dto.ResultCustomerCheck;
import com.alessandro.order_service.messaging.rabbitmq.config.MessagingConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessageHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MessagingConfig.RESULT_CHECK_CUSTOMER_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void checkCustomerAndSendCheckProducts(ResultCustomerCheck resultCustomerCheck) {
//        System.out.println("Message received from queue "+MessagingConfig.RESULT_CHECK_CUSTOMER_QUEUE_NAME);
//        System.out.println(resultCustomerCheck.getCustomer());
        Order o = orderRepository.findById(resultCustomerCheck.getOrderId()).get();
        Customer c = resultCustomerCheck.getCustomer();
        if(c == null) {
            o.setState("ANNULLATO");
            o.setMessageState(resultCustomerCheck.getMessageResult());
        }else{
            o.setNomeCliente(c.getNome());
            o.setCognomeCliente(c.getCognome());
            rabbitTemplate.convertAndSend(
                    MessagingConfig.EXCHANGER_ORDER_SERVICE_NAME,
                    MessagingConfig.ROUTINGKEY_CHECK_PRODUCTS_NAME,
                    new ProductsOL(o.getLineaOrdine(), o.getId())
            );
        }
    }

    @RabbitListener(queues = MessagingConfig.RESULT_CHECK_PRODUCTS_QUEUE_NAME)
    @Transactional(readOnly = false)
    public void resultCheckProducts(ProductsOL message) {
        System.out.println("resultCheckProducts");
        Order o = orderRepository.findById(message.getOrderId()).get();
        if(message.getOrderLineList() == null){
            //rimborsare il cliente
            System.out.println("rimborsare il cliente");
            rabbitTemplate.convertAndSend(
                    MessagingConfig.EXCHANGER_ORDER_SERVICE_NAME,
                    MessagingConfig.ROUTINGKEY_CREDIT_ROLLBACK_NAME,
                    new MessageCustomerRollback(o.getIdCliente(), o.getTotale())
            );
            //impostare lo stato dell'ordine su annullato
            o.setState("ANNULLATO");
            o.setMessageState(message.getMessage());
        }else{
            o.setState("CONFERMATO");
            o.setMessageState("ordine confermato");
        }
    }


}
