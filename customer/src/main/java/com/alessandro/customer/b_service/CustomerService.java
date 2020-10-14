package com.alessandro.customer.b_service;

import com.alessandro.customer.c_repository.CustomerRepository;
import com.alessandro.customer.d_entity.Customer;
import com.alessandro.customer.support.exception.CustomerNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(int id) throws CustomerNotExistException {
        Optional<Customer> c = customerRepository.findById(id);
        if(!c.isPresent())
            throw new CustomerNotExistException();
        return c.get();
    }

    @Transactional
    public List<Customer> generaCienti(int inizio, int fine) {
        for(int i = inizio; i<= fine; i++){
            customerRepository.save(new Customer("nomeCustomer_"+i,"cognomeCustomer_"+i));
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return customerRepository.findAll();
    }
}
