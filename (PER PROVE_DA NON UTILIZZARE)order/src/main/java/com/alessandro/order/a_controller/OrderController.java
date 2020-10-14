package com.alessandro.order.a_controller;

import com.alessandro.order.b_service.OrderService;
import com.alessandro.order.d_entity.Order;
import com.alessandro.order.support.exception.OrderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/order_api")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(value = "/order_customer/{idCustomer}")
    public ResponseEntity getOrderCustomer(@PathVariable int idCustomer){
        return new ResponseEntity(orderService.customerOrder(idCustomer), HttpStatus.OK);
    }

    @PostMapping(value = "/create_order")
    public ResponseEntity createOrder(@RequestBody Order order) {
        try {
            return new ResponseEntity(orderService.create(order), HttpStatus.CREATED);
        } catch (OrderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ordine non creato -> "+e.getMessage(), e);
        }
    }
}
