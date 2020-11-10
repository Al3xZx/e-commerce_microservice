package com.alessandro.order_service.b_service;

import com.alessandro.order_service.c_repository.OrderRepository;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.d_entity.OrderLine;
import com.alessandro.order_service.support.exception.OrderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderService {

    @Value("${apigateway.ip}")
    private String ipApiGateway;

    @Autowired
    OrderRepository orderRepository;


    /**
     * @param order
     * @return Persist order
     * @throws OrderException
     */
    @Transactional(readOnly = false)
    public Order create(Order order) throws OrderException {//TODO Test

        Map<Integer, Integer> backupProdottoQta = new HashMap<>();
        try {
            Order ret = new Order();
            setCustomer(ret, order.getIdCliente());
            for (OrderLine ol : order.getLineaOrdine()) {
                setProduct(ol,backupProdottoQta);
                ol.setOrdine(ret);
                ret.getLineaOrdine().add(ol);
            }

            orderRepository.save(ret);
            return ret;
        }catch (OrderException e){
            if(!backupProdottoQta.isEmpty()){
                RestTemplate restTemplate = new RestTemplate();
                for(int idP : backupProdottoQta.keySet()){
                    restTemplate.put(
                            "https://{ip}/product_service/product/{idProdotto}/update_qta/{qta}",
                            null, this.ipApiGateway, idP, backupProdottoQta.get(idP)
                    );
                }
            }
            throw new OrderException(e.getMessage());
        }

    }

    /**
     * effettua la richiesta al servizio che gestisce i prodotti per ottenere i dati relativi al prodotto verificando
     * che la quantità richiesta è effettivamente disponibile
     * @param ol
     * @throws OrderException
     */
    private void setProduct(OrderLine ol, Map<Integer, Integer> bk) throws OrderException {
        RestTemplate restTemplate = new RestTemplate();

        String JsonProduct;
        try {
            JsonProduct = restTemplate.getForObject(
                    "https://{ip}/product_service/product/{id}",
                    String.class, this.ipApiGateway, ol.getIdProdotto()
            );
        }catch (HttpClientErrorException e){
            throw new OrderException("errore nel reperire i dati del prdotto "+ol.getIdProdotto());
        }
        System.out.println("prelevato prodotto "+JsonProduct);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(JsonProduct);
            ol.setNomeProdotto(rootNode.path("name").asText());
            if(ol.getQta() < 0)
                throw new OrderException(String.format("la quantità richiesta per il prodotto %d non è ammessa",ol.getIdProdotto()));
            if(ol.getQta().compareTo( rootNode.path("qta").asInt() ) > 0)
                throw new OrderException("la quantità disponibile del prodotto "+ ol.getNomeProdotto()+" è inferiore a quella richiesta");
            bk.put(ol.getIdProdotto(),ol.getQta());
            restTemplate.put(
                    "https://{ip}/product_service/product/{idProdotto}/update_qta/{qta}",
                    null,
                    this.ipApiGateway,
                    ol.getIdProdotto(),
                    ol.getQta()*-1
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Effettua una richiesta al servizio dei clienti per ottenere i dati relativi al cliente che ha effettuato l'ordine
     * @param o
     * @param idCliente
     * @throws OrderException
     */
    private void setCustomer(Order o, int idCliente) throws OrderException {
        RestTemplate restTemplate = new RestTemplate();
        String JsonCustomer;
        try{
            JsonCustomer = restTemplate.getForObject(
                    "https://{ip}/customer_service/customer/{id}",
                    String.class, this.ipApiGateway,idCliente
            );
        }catch (HttpClientErrorException e){
            throw new OrderException("errore nel reperire i dati del cliente "+idCliente);
        }
        System.out.println("prelevato cliente"+JsonCustomer);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(JsonCustomer);
            o.setNomeCliente(rootNode.path("nome").asText());
            o.setCognomeCliente(rootNode.path("cognome").asText());
            o.setIdCliente(idCliente);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<Order> customerOrder(int idCustomer){
        return orderRepository.findByIdCliente(idCustomer);
    }
}
