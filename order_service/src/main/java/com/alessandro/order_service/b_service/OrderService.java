package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.d_entity.OrderLine;
import com.alessandro.order_service.messaging.dto.MessageCustomerCheck;
import com.alessandro.order_service.messaging.rabbitmq.config.MessagingConfig;
import com.alessandro.order_service.support.exception.OrderException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

        //salvo ordeine temporaneo
        orderRepository.save(order);

        //invio messaggio di verifica del cliente
        rabbitTemplate.convertAndSend(
                MessagingConfig.EXCHANGER_ORDER_SERVICE_NAME,
                MessagingConfig.ROUTINGKEY_CHECK_CUSTOMER_NAME,
                new MessageCustomerCheck(order.getIdCliente(), order.getId(), totaleOrdine)
        );
        return order;
    }


}
