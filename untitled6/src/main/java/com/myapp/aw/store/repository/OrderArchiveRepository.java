package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.OrderArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderArchiveRepository extends JpaRepository<OrderArchive, Long> {

    // Original method to find all orders for a customer
    List<OrderArchive> findByCustomerId(Long customerId);

    List<OrderArchive> findByOrderPlacementTimeBetween(LocalDateTime start, LocalDateTime end);

    // New method to find orders for a customer with pagination
    Page<OrderArchive> findByCustomerId(Long customerId, Pageable pageable);
}