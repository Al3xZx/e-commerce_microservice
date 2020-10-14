package com.alessandro.order.b_service;

import com.alessandro.order.c_repository.LineaOrdineRepository;
import com.alessandro.order.c_repository.OrderRepository;
import com.alessandro.order.d_entity.LineaOrdine;
import com.alessandro.order.d_entity.Order;
import com.alessandro.order.support.exception.OrderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    LineaOrdineRepository lineaOrdineRepository;


    //nuovo ordine
    @Transactional(readOnly = false)
    public Order create(Order order) throws OrderException {
        Order ret = new Order();
        setCustomer(ret, order.getIdCliente());
        for(LineaOrdine ol : order.getLineaOrdine()){
            setProduct(ol);
            ol.setOrdine(ret);
            ret.getLineaOrdine().add(ol);
        }
        orderRepository.save(ret);
        return ret;

    }

    private void setProduct(LineaOrdine ol) throws OrderException {
        RestTemplate restTemplate = new RestTemplate();

        String JsonProduct;
        try {
            //TODO cambiare url con api gateway url
            JsonProduct = restTemplate.getForObject("http://localhost:8091/product_api/product/{id}", String.class,ol.getIdProdotto());
        }catch (HttpClientErrorException e){
            throw new OrderException("errore nel reperire i dati del prdotto "+ol.getIdProdotto());
        }
        System.out.println("prelevato prodotto "+JsonProduct);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(JsonProduct);
            ol.setNomeProdotto(rootNode.path("name").asText());
            if(ol.getQta().compareTo( rootNode.path("qta").asInt() ) > 0)
                throw new OrderException("la quantità disponibile del prodotto "+ ol.getNomeProdotto()+" è inferiore a quella richiesta");
            int newQta = rootNode.path("qta").asInt() - ol.getQta();
            restTemplate.put("http://localhost:8091/product_api/set_qta/"+ol.getIdProdotto()+"/"+newQta,null);

            /**ATTENZIONE così facendo si modifica prima dell'effettivo inserimento di un ordine la quantità disponibile dei prodotti
             * nel caso in cui si verifica la non disponibilità dei prodotti successivi a questo l'ordine non verrà registrato ma la quantità
             * dei prodotti è stata modificata*/


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void setCustomer(Order o, int idCliente) throws OrderException {
        RestTemplate restTemplate = new RestTemplate();
        String JsonCustomer;
        try{
            //TODO cambiare url con api gateway url
            JsonCustomer = restTemplate.getForObject("http://localhost:8090/customer_api/customer/{id}", String.class,idCliente);
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
