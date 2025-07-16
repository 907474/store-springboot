package com.myapp.aw.store.repository;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.ProductStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void whenSaveProduct_thenItShouldBeFound() {
        Product newProduct = new Product();
        newProduct.setProductName("Test Laptop");
        newProduct.setProductSku("SKU-TEST-001");
        newProduct.setProductPrice(new BigDecimal("999.99"));
        newProduct.setProductQuantity(50);
        newProduct.setType("Electronics");
        newProduct.setStatus(ProductStatus.DISPLAY);

        Product savedProduct = productRepository.save(newProduct);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductId()).isGreaterThan(0);

        Optional<Product> foundProductOpt = productRepository.findById(savedProduct.getProductId());

        assertThat(foundProductOpt).isPresent();
        assertThat(foundProductOpt.get().getProductName()).isEqualTo("Test Laptop");
    }
}