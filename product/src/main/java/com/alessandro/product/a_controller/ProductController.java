package com.alessandro.product.a_controller;

import com.alessandro.product.b_service.ProductService;
import com.alessandro.product.d_entity.Product;
import com.alessandro.product.support.exception.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(value = "/product")
    public ResponseEntity productList(){
        return new ResponseEntity(productService.getAllProduct(), HttpStatus.OK);
    }

    @GetMapping(value = "/product/{idProduct}")
    public ResponseEntity<Product> product(@PathVariable int idProduct){
        try {
            return new ResponseEntity(productService.getProduct(idProduct),HttpStatus.OK);
        } catch (ProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(), e);
        }
    }

    @PutMapping(value = "/product/{prodId}/update_qta/{qta}")
    public ResponseEntity updateQta(@PathVariable int prodId,@PathVariable int qta){
        try {
            return new ResponseEntity(productService.updateQta(prodId, qta),HttpStatus.OK);
        } catch (ProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage(), e);
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
        if(productService.getAllProduct().size() != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(productService.generaProdotti(inizio, fine),HttpStatus.CREATED);
    }
}
