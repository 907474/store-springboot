package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.OrderStatus;
import com.myapp.aw.store.model.TemporaryOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporaryOrderRepository extends JpaRepository<TemporaryOrder, Long> {

    Optional<TemporaryOrder> findByOrderIdAndStatus(Long orderId, OrderStatus status);

    Page<TemporaryOrder> findAllByStatus(OrderStatus status, Pageable pageable);

    List<TemporaryOrder> findAllByStatus(OrderStatus status);
}