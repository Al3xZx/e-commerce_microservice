package com.alessandro.order_service.a_cotroller;

import com.alessandro.order_service.b_service.OrderService;
import com.alessandro.order_service.d_entity.Order;
import com.alessandro.order_service.support.exception.OrderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/order_service")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(value = "/orders/{idCustomer}")
    public ResponseEntity getOrderCustomer(@PathVariable int idCustomer){
        return new ResponseEntity(orderService.customerOrder(idCustomer), HttpStatus.OK);
    }

    @PostMapping(value = "/order")
    public ResponseEntity createOrder(@RequestBody Order order) {
        try {
            return new ResponseEntity(orderService.create(order), HttpStatus.CREATED);
        } catch (OrderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ordine non creato -> "+e.getMessage(), e);
        }
    }
}
