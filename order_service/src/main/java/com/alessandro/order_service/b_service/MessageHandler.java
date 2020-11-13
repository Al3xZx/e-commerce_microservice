package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.messaging.dto.Customer;
import com.alessandro.order_service.messaging.dto.MessageCustomerRollback;
import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.dto.ResultCustomerCheck;
import com.alessandro.order_service.messaging.pubsub.conf.PubSubConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;


@Service
public class MessageHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    PubSubTemplate pubSubTemplate;


    @Bean
    public void readResultCheckCustomer(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.RESULT_CHECK_CUSTOMER_SUBSCRIPTION,
                (message) ->{
                    ResultCustomerCheck r = message.getPayload();
                    message.ack();
                    checkCustomerAndSendCheckProducts(r);
                },
                ResultCustomerCheck.class
        );
    }

    @Transactional(readOnly = false)
    public void checkCustomerAndSendCheckProducts(ResultCustomerCheck resultCustomerCheck) {
        System.out.println("Message received from queue "+PubSubConf.RESULT_CHECK_CUSTOMER_SUBSCRIPTION);
        System.out.println(resultCustomerCheck.getCustomer());
        Order o = orderRepository.findById(resultCustomerCheck.getOrderId()).get();
        Customer c = resultCustomerCheck.getCustomer();
        if(c == null) {
            o.setState("ANNULLATO");
            o.setMessageState(resultCustomerCheck.getMessageResult());
            orderRepository.saveAndFlush(o);
        }else{
            o.setNomeCliente(c.getNome());
            o.setCognomeCliente(c.getCognome());
            orderRepository.saveAndFlush(o);
            System.out.println("invio messaggio verifica dei prodotti");
            //NON VIENE INVIATO (non veniva inviato poichè il fetch type di order era lazy dunque non prelevava la Linea d'ordine)
            pubSubTemplate.publish(PubSubConf.CHECK_PRODUCTS_TOPIC, new ProductsOL(o.getLineaOrdine(), o.getId()));
            System.out.println("messaggio inserito nel topic " + PubSubConf.CHECK_PRODUCTS_TOPIC);

        }
        
    }

    @Bean
    public void readResultCheckProducts(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.RESULT_CHECK_PRODUCTS_SUBSCRIPTION,
                (message) ->{
                    ProductsOL m = message.getPayload();
                    message.ack();
                    resultCheckProducts(m);
                },
                ProductsOL.class
        );
    }
    
    @Transactional(readOnly = false)
    public void resultCheckProducts(ProductsOL message) {
        System.out.println("resultCheckProducts");
        Order o = orderRepository.findById(message.getOrderId()).get();
        if(message.getOrderLineList() == null){
            //rimborsare il cliente
            System.out.println("rimborsare il cliente");
            pubSubTemplate.publish(PubSubConf.CREDIT_ROLLBACK_TOPIC, new MessageCustomerRollback(o.getIdCliente(), o.getTotale()));
            //impostare lo stato dell'ordine su annullato
            o.setState("ANNULLATO");
            o.setMessageState(message.getMessage());
            orderRepository.saveAndFlush(o);
        }else{
            o.setState("CONFERMATO");
            o.setMessageState("ordine confermato");
            orderRepository.saveAndFlush(o);
        }
    }
}
