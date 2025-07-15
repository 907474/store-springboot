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
        // Arrange: Create a new product
        Product newProduct = new Product();
        newProduct.setProductName("Test Laptop");
        newProduct.setProductSku("SKU-TEST-001");
        newProduct.setProductPrice(new BigDecimal("999.99"));
        newProduct.setProductQuantity(50);
        newProduct.setType("Electronics");
        newProduct.setStatus(ProductStatus.DISPLAY);

        // Act: Save the product to the database
        Product savedProduct = productRepository.save(newProduct);

        // Assert: Check that the product was saved and has an ID
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getProductId()).isGreaterThan(0);

        // Act: Find the product by its new ID
        Optional<Product> foundProductOpt = productRepository.findById(savedProduct.getProductId());

        // Assert: Check that the found product exists and its details are correct
        assertThat(foundProductOpt).isPresent();
        assertThat(foundProductOpt.get().getProductName()).isEqualTo("Test Laptop");
    }
}