package com.alessandro.product.b_service;

import com.alessandro.product.c_repository.ProductRepository;
import com.alessandro.product.d_entity.Product;
import com.alessandro.product.messaging.dto.OrderLine;
import com.alessandro.product.messaging.dto.ProductsOL;
import com.alessandro.product.messaging.pubsub.conf.PubSubConf;
import com.alessandro.product.support.exception.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MessageHandler {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PubSubTemplate pubSubTemplate;


    @Bean
    public void readMessage(){
        pubSubTemplate.subscribeAndConvert(
                PubSubConf.CHECK_PRODUCTS_SUBSCRIPTION,
                (message) ->{
                    ProductsOL p = message.getPayload();
                    message.ack();
                    checkProducts(p);

                },
                ProductsOL.class
        );
    }

    public void checkProducts(ProductsOL message){
        System.out.println("Message received from topic "+PubSubConf.CHECK_PRODUCTS_SUBSCRIPTION);
        try {
            checkProductsAndUpdateQty(message);
        } catch (ProductException e) {
            pubSubTemplate.publish(
                    PubSubConf.RESULT_CHECK_PRODUCTS_TOPIC,
                    new ProductsOL(null, message.getOrderId(), e.getMessage())
            );
        }
    }//checkProductsAndUpdateQty

    @Transactional(readOnly = false, rollbackFor = ProductException.class)
    public void checkProductsAndUpdateQty(ProductsOL message) throws ProductException {
        List<Product> products = new LinkedList<>();
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
            products.add(p);
        }//for
        for (Product p : products){
            productRepository.saveAndFlush(p);
        }
        pubSubTemplate.publish(
                PubSubConf.RESULT_CHECK_PRODUCTS_TOPIC,
                new ProductsOL(message.getOrderLineList(), message.getOrderId(),  "verifica effettuata con successo")
        );
    }
}
