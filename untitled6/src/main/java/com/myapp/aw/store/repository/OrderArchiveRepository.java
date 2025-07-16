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

    List<OrderArchive> findByCustomerId(Long customerId);

    List<OrderArchive> findByOrderPlacementTimeBetween(LocalDateTime start, LocalDateTime end);

    Page<OrderArchive> findByCustomerId(Long customerId, Pageable pageable);
}