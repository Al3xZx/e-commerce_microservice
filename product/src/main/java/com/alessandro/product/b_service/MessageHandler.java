package com.alessandro.product.b_service;

import com.alessandro.product.c_repository.ProductRepository;
import com.alessandro.product.d_entity.Product;
import com.alessandro.product.messaging.dto.OrderLine;
import com.alessandro.product.messaging.dto.ProductsOL;
import com.alessandro.product.messaging.rabbitmq.config.MessagingConfig;
import com.alessandro.product.support.exception.ProductException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MessageHandler {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MessagingConfig.CHECK_PRODUCTS_QUEUE_NAME)
    public void checkProducts(ProductsOL message){
        System.out.println("Message received from queue "+MessagingConfig.CHECK_PRODUCTS_QUEUE_NAME);
        try {
            checkProductsAndUpdateQty(message);
        } catch (ProductException e) {
            rabbitTemplate.convertAndSend(
                    MessagingConfig.EXCHANGER_PRODUCT_SERVICE_NAME,
                    MessagingConfig.ROUTINGKEY_RESULT_CHECK_PRODUCTS_NAME,
                    new ProductsOL(
                            null,
                            message.getOrderId(),
                            e.getMessage()
                    )
            );
        }
    }//checkProductsAndUpdateQty

    @Transactional(readOnly = false, rollbackFor = ProductException.class)
    public void checkProductsAndUpdateQty(ProductsOL message) throws ProductException {
        for(OrderLine ol: message.getOrderLineList()) {
            System.out.println("--order --> "+ol.getId() + " --order id --> " + message.getOrderId());
            Optional<Product> op = productRepository.findById(ol.getIdProdotto());
            if(!op.isPresent()){
                throw new ProductException(String.format("prodotto %d non presente", ol.getIdProdotto()));
            }
            Product p = op.get();
            if(p.getQta().compareTo(ol.getQta()) < 0){
                throw new ProductException(String.format("la quantità del prodotto %d non è sufficiente", ol.getIdProdotto()));
            }
            if(p.getPrice().compareTo(ol.getPrezzoProdotto()) != 0){
                throw new ProductException(String.format("Il prezzo del prodotto %d non coincide", ol.getIdProdotto()));
            }
            ol.setNomeProdotto(p.getName());
            p.setQta(p.getQta()-ol.getQta());
        }//for

        rabbitTemplate.convertAndSend(
                MessagingConfig.EXCHANGER_PRODUCT_SERVICE_NAME,
                MessagingConfig.ROUTINGKEY_RESULT_CHECK_PRODUCTS_NAME,
                new ProductsOL(
                        message.getOrderLineList(),
                        message.getOrderId(),
                        "verifica effettuata con successo"
                )
        );
    }
}
