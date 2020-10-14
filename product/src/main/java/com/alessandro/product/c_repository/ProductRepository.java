package com.alessandro.product.c_repository;

import com.alessandro.product.d_entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

//    @Modifying
//    @Query(value =  "UPDATE product " +
//                    "SET qta = ?2 " +
//                    "WHERE id = ?1", nativeQuery = true)
//    void updateQta(int prodId, int qta);
}
