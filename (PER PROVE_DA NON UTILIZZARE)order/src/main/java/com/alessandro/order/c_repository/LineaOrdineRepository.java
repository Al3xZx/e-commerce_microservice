package com.alessandro.order.c_repository;

import com.alessandro.order.d_entity.LineaOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaOrdineRepository extends JpaRepository<LineaOrdine, Integer> {
}
