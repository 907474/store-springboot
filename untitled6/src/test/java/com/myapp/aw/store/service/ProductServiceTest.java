package com.myapp.aw.store.service;

import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.ProductStatus;
import com.myapp.aw.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void whenCreateProductWithUniqueSku_thenProductShouldBeSaved() {
        // Arrange: Define the behavior of the mock repository
        when(productRepository.findByProductSku("NEW-SKU")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the service method to create a new product
        Product newProduct = productService.createProduct(
                "New Product", "NEW-SKU", new BigDecimal("10.00"),
                100, "Test Type", ProductStatus.DISPLAY
        );

        // Assert: Check that the product details are correct
        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getProductName()).isEqualTo("New Product");

        // Assert: Verify that the save method on the repository was called exactly once
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void whenCreateProductWithExistingSku_thenThrowException() {
        // Arrange: Tell the mock repository that a product with this SKU already exists
        when(productRepository.findByProductSku("EXISTING-SKU")).thenReturn(Optional.of(new Product()));

        // Act & Assert: Check that calling the service method throws the expected exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productService.createProduct(
                    "Another Product", "EXISTING-SKU", new BigDecimal("20.00"),
                    50, "Test Type", ProductStatus.DISPLAY
            );
        });

        // Assert: Check that the error message is correct
        assertThat(exception.getMessage()).isEqualTo("SKU 'EXISTING-SKU' already exists.");

        // Assert: Verify that the save method was never called
        verify(productRepository, never()).save(any(Product.class));
    }
}