package com.alessandro.product.b_service;

import com.alessandro.product.c_repository.ProductRepository;
import com.alessandro.product.d_entity.Product;
import com.alessandro.product.support.exception.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProduct(int id) throws ProductException {
        Optional<Product> p = productRepository.findById(id);
        if(!p.isPresent())
            throw new ProductException("prodotto non trovato");
        return p.get();
    }

    /**
     *
     * @param prodId
     * @param qta può essere positivo o negativo in base se la quantità va aggiunta o sottratta
     * @return
     * @throws ProductException
     */
    @Transactional(readOnly = false)
    public Product updateQta (int prodId, int qta) throws ProductException {
        Optional<Product> p = productRepository.findById(prodId);
        if(!p.isPresent())
            throw new ProductException("prodotto non trovato");
        Product prod = p.get();
        prod.setQta(prod.getQta()+qta);
        return prod;
    }

    @Transactional
    public List<Product> generaProdotti(int inizio, int fine){
        //inizio e fine inclusi
        for(int i = inizio; i<= fine; i++){
            productRepository.save(new Product("nomePrdotto_"+i,150));
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return productRepository.findAll();
    }
}
