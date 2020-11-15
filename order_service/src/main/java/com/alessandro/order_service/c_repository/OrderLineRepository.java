package com.alessandro.order_service.c_repository;

import com.alessandro.order_service.d_entity.OrderLine;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends DatastoreRepository<OrderLine, Integer> {

}
