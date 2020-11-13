package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.d_entity.OrderLine;
import com.alessandro.order_service.messaging.dto.MessageCustomerCheck;
import com.alessandro.order_service.messaging.dto.ProductsOL;
import com.alessandro.order_service.messaging.pubsub.conf.PubSubConf;
import com.alessandro.order_service.support.exception.OrderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PubSubTemplate pubSubTemplate;

    public List<Order> customerOrder(int idCustomer){
        return orderRepository.findByIdCliente(idCustomer);
    }

    /**
     * @param order
     * @return Persist order
     * @throws OrderException
     */
    @Transactional(readOnly = false)
    public Order create(Order order) throws OrderException {
        order.setState("SOSPESO");
        order.setMessageState("ordine in sospeso");

        //calcolo totale parziale di ogni linea ordine e determino il totale dell'ordine
        double totaleOrdine = 0;
        for(OrderLine ol : order.getLineaOrdine()){
            ol.setOrdine(order);

            double totaleOL = ol.getPrezzoProdotto()*ol.getQta();
            totaleOL = Math.floor(totaleOL*100)/100; //prelevo le prime due cifre decimali
            ol.setTotale(totaleOL);
            totaleOrdine += totaleOL;
        }
        totaleOrdine = Math.floor(totaleOrdine*100)/100;//prelevo le prime due cifre decimali
        order.setTotale(totaleOrdine);

        //salvo ordine temporaneo
        orderRepository.save(order);

        pubSubTemplate.publish(
                PubSubConf.CHECK_CUSTOMER_TOPIC,
                new MessageCustomerCheck(order.getIdCliente(), order.getId(), totaleOrdine)
        );
        System.out.println("messaggio inviato: {" + order.getIdCliente() + ", " + order.getId() + ", " + totaleOrdine + "}");

//        pubSubTemplate.publish(
//                PubSubConf.CHECK_PRODUCTS_TOPIC,
//                new ProductsOL(order.getLineaOrdine(), order.getId())
//        );
        return order;
    }

}
