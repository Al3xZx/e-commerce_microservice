package com.alessandro.order_service.c_repository;

import com.alessandro.order_service.d_entity.Order;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends DatastoreRepository<Order, Integer> {
    List<Order> findByIdCliente(int id);
}
