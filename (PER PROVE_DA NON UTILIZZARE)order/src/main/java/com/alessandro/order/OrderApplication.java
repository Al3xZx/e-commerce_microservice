package com.alessandro.order;

import com.alessandro.order.d_entity.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;


@SpringBootApplication
public class OrderApplication {



    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
