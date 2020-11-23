package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderLineRepository;
import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.d_entity.OrderLine;
import com.alessandro.order_service.messaging.dto.Customer;
import com.alessandro.order_service.messaging.dto.MessageCustomerRollback;
import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.dto.ResultCustomerCheck;
import com.alessandro.order_service.messaging.pubsub.conf.PubSubConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessageHandler implements ApplicationListener<ApplicationReadyEvent> {

    private Log log = LogFactory.getLog(MessageHandler.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    OrderLineRepository orderLineRepository;

    @Autowired
    PubSubTemplate pubSubTemplate;


    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        readResultCheckCustomer();
        readResultCheckProducts();
    }

    public void readResultCheckCustomer(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.RESULT_CHECK_CUSTOMER_SUBSCRIPTION,
                (message) ->{
                    ResultCustomerCheck r = message.getPayload();
                    checkCustomerAndSendCheckProducts(r);
                    message.ack();
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
            pubSubTemplate.publish(PubSubConf.CHECK_PRODUCTS_TOPIC, new ProductsOL(o.getLineaOrdine(), o.getId()));
            System.out.println("messaggio inserito nel topic " + PubSubConf.CHECK_PRODUCTS_TOPIC);
        }

    }


    public void readResultCheckProducts(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.RESULT_CHECK_PRODUCTS_SUBSCRIPTION,
                (message) ->{
                    ProductsOL m = message.getPayload();
                    resultCheckProducts(m);
                    message.ack();
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
            log.info("rimborso cliente "+o.getIdCliente());
            pubSubTemplate.publish(PubSubConf.CREDIT_ROLLBACK_TOPIC, new MessageCustomerRollback(o.getIdCliente(), o.getTotale()));
            //impostare lo stato dell'ordine su annullato
            o.setState("ANNULLATO");
            o.setMessageState(message.getMessage());
        }else {
            //aggiorno nome prodotto
            for (OrderLine ol : o.getLineaOrdine()){
                for (OrderLine oltmp : message.getOrderLineList()) {
                    if (oltmp.getIdProdotto().equals(ol.getIdProdotto())) {
                        ol.setNomeProdotto(oltmp.getNomeProdotto());
                        orderLineRepository.saveAndFlush(ol);
                        break;
                    }
                }
            }
            o.setState("CONFERMATO");
            o.setMessageState("ordine confermato");
        }
        orderRepository.saveAndFlush(o);
    }


}
