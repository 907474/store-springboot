package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
            "(LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.productSku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByStatusAndNameOrSku(
            @Param("status") ProductStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.productSku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameOrSkuContainingIgnoreCase(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<Product> findByProductSku(String productSku);

    long countByProductQuantityLessThan(int stockLevel);

    List<Product> findByProductQuantityLessThan(int stockLevel);
}