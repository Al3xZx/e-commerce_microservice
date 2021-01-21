package com.alessandro.customer.a_controller;

import com.alessandro.customer.b_service.CustomerService;
import com.alessandro.customer.d_entity.Customer;
import com.alessandro.customer.support.exception.CustomerNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class CustomerController{

    @Autowired
    CustomerService customerService;

//    private List<Customer> customers = new LinkedList<>();

    @GetMapping(value = "/customer")
    public ResponseEntity customers(){
        return new ResponseEntity(customerService.getAllCustomer(), HttpStatus.OK);
    }

    @GetMapping(value = "/customer/{idCust}")
    public ResponseEntity customer(@PathVariable int idCust){
        try {
            return new ResponseEntity(customerService.getCustomer(idCust),HttpStatus.OK);
        } catch (CustomerNotExistException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente non trovato", e);
        }
    }

    /**
     * SOLO PER GENERARE DATI PER I TEST
     * @param inizio
     * @param fine
     * @return
     */
    @PostMapping(value = "for_admin/genera/{inizio}/{fine}")
    public ResponseEntity genera(@PathVariable int inizio,@PathVariable int fine){
        if(customerService.getAllCustomer().size() != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        customerService.generaCienti(inizio, fine);
        return new ResponseEntity(HttpStatus.CREATED);
    }

//    @Override
//    public void onApplicationEvent(final ApplicationReadyEvent event) {
//        generateCustomers();
//    }
//
//    public void generateCustomers(){
//        for (int i = 0; i < 10; i++)
//            customers.add(new Customer("Nome_Customer"+i, "Cognome_Customer"+i));
//    }
}
