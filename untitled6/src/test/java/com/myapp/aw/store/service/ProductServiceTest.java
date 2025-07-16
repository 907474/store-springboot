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
        when(productRepository.findByProductSku("NEW-SKU")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product newProduct = productService.createProduct(
                "New Product", "NEW-SKU", new BigDecimal("10.00"),
                100, "Test Type", ProductStatus.DISPLAY
        );

        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getProductName()).isEqualTo("New Product");

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void whenCreateProductWithExistingSku_thenThrowException() {
        when(productRepository.findByProductSku("EXISTING-SKU")).thenReturn(Optional.of(new Product()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productService.createProduct(
                    "Another Product", "EXISTING-SKU", new BigDecimal("20.00"),
                    50, "Test Type", ProductStatus.DISPLAY
            );
        });

        assertThat(exception.getMessage()).isEqualTo("SKU 'EXISTING-SKU' already exists.");

        verify(productRepository, never()).save(any(Product.class));
    }
}