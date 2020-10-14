package com.alessandro.order_service.c_repository;

import com.alessandro.order_service.d_entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByIdCliente(int id);
}
