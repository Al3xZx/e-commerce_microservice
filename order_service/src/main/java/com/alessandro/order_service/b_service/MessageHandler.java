package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.d_entity.OrderLine;
import com.alessandro.order_service.messaging.dto.Customer;
import com.alessandro.order_service.messaging.dto.MessageCustomerRollback;
import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.dto.ResultCustomerCheck;
import com.alessandro.order_service.messaging.pubsub.conf.PubSubConf;
import com.alessandro.order_service.messaging.pubsub.conf.SendMsgConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;


@Service
public class MessageHandler {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    SendMsgConfig.CheckProductsOutboundGateway checkProductsOutboundGateway;

    @Autowired
    SendMsgConfig.CreditRollbackOutboundGateway creditRollbackOutboundGateway;


    @ServiceActivator(inputChannel = "resultCheckCustomerInputChannel")
    public void readResultCheckCustomer(ResultCustomerCheck payload){
        checkCustomerAndSendCheckProducts(payload);

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

            List<OrderLine> l = o.getLineaOrdine();
            for(OrderLine ol : l){
                System.out.println(ol.getIdProdotto());
            }
            //checkProductsOutboundGateway.sendCheckProductsToPubSub(new ProductsOL(l, o.getId()));
            System.out.println("messaggio inserito nel topic " + PubSubConf.CHECK_PRODUCTS_TOPIC);

        }
        
    }

    @ServiceActivator(inputChannel = "resultCheckProductInputChannel")
    public void readResultCheckProducts(ProductsOL payload){
        resultCheckProducts(payload);
    }
    
    @Transactional(readOnly = false)
    public void resultCheckProducts(ProductsOL message) {
        System.out.println("resultCheckProducts");
        Order o = orderRepository.findById(message.getOrderId()).get();
        if(message.getOrderLineList() == null){
            //rimborsare il cliente
            System.out.println("rimborsare il cliente");
            creditRollbackOutboundGateway.sendCreditRollbackToPubSub(new MessageCustomerRollback(o.getIdCliente(), o.getTotale()));
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
