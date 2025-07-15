package com.myapp.aw.store.service;

import com.alibaba.excel.EasyExcel;
import com.myapp.aw.store.model.Product;
import com.myapp.aw.store.model.ProductStatus;
import com.myapp.aw.store.model.dto.BulkUpdateResultDTO;
import com.myapp.aw.store.model.dto.ProductExcelDTO;
import com.myapp.aw.store.repository.ProductRepository;
import com.myapp.aw.store.service.excel.ProductDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(
            String name, String sku, BigDecimal price,
            int quantity, String type, ProductStatus status
    ) {
        log.info("Attempting to create new product with SKU: {}", sku);
        productRepository.findByProductSku(sku).ifPresent(p -> {
            log.warn("Product creation failed: SKU '{}' already exists.", sku);
            throw new IllegalStateException("SKU '" + sku + "' already exists.");
        });

        Product product = new Product();
        product.setProductName(name);
        product.setProductSku(sku);
        product.setProductPrice(price);
        product.setProductQuantity(quantity);
        product.setType(type);
        product.setStatus(status);

        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product '{}' with ID: {}", savedProduct.getProductName(), savedProduct.getProductId());
        return savedProduct;
    }

    @Transactional
    public void updateProduct(
            Long productId, String name, String sku,
            BigDecimal price, int quantity, String type, ProductStatus status
    ) {
        log.info("Attempting to update product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productRepository.findByProductSku(sku).ifPresent(p -> {
            if (!p.getProductId().equals(productId)) {
                log.warn("Product update failed: SKU '{}' is already in use by product ID {}", sku, p.getProductId());
                throw new IllegalStateException("SKU '" + sku + "' is already in use by another product.");
            }
        });

        product.setProductName(name);
        product.setProductSku(sku);
        product.setProductPrice(price);
        product.setProductQuantity(quantity);
        product.setType(type);
        product.setStatus(status);

        productRepository.save(product);
        log.info("Successfully updated product ID: {}", productId);
    }

    public byte[] generateProductsExcel() {
        log.info("Generating Excel download for all products.");
        List<Product> products = productRepository.findAll();
        List<ProductExcelDTO> dtoList = products.stream().map(this::convertToDto).collect(Collectors.toList());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ProductExcelDTO.class).sheet("Products").doWrite(dtoList);

        log.debug("Excel file generated with {} rows.", dtoList.size());
        return outputStream.toByteArray();
    }

    @Transactional
    public BulkUpdateResultDTO processExcelUpload(MultipartFile file) throws IOException {
        log.info("Processing Excel upload for file: {}", file.getOriginalFilename());
        ProductDataListener listener = new ProductDataListener();
        EasyExcel.read(file.getInputStream(), ProductExcelDTO.class, listener).sheet().doRead();
        List<ProductExcelDTO> dtoList = listener.getReadData();

        List<String> updatedDetails = new ArrayList<>();
        List<String> createdProductNames = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<Product> productsToSave = new ArrayList<>();

        List<ProductExcelDTO> updates = dtoList.stream()
                .filter(d -> d.getProductId() != null && d.getProductId() != 0L)
                .toList();
        List<ProductExcelDTO> creations = dtoList.stream()
                .filter(d -> d.getProductId() == null || d.getProductId() == 0L)
                .toList();

        log.debug("Found {} products to update and {} products to create.", updates.size(), creations.size());

        if (!updates.isEmpty()) {
            List<Long> ids = updates.stream().map(ProductExcelDTO::getProductId).toList();
            Map<Long, Product> existingProductsMap = productRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(Product::getProductId, Function.identity()));

            for (ProductExcelDTO dto : updates) {
                Product product = existingProductsMap.get(dto.getProductId());
                if (product != null) {
                    StringBuilder changes = new StringBuilder();
                    if (!Objects.equals(product.getProductName(), dto.getProductName())) { changes.append(String.format("Name ('%s' -> '%s'), ", product.getProductName(), dto.getProductName())); product.setProductName(dto.getProductName()); }
                    if (!Objects.equals(product.getProductSku(), dto.getProductSku())) { changes.append(String.format("SKU ('%s' -> '%s'), ", product.getProductSku(), dto.getProductSku())); product.setProductSku(dto.getProductSku()); }
                    if (product.getProductPrice().compareTo(dto.getProductPrice()) != 0) { changes.append(String.format("Price (%.2f -> %.2f), ", product.getProductPrice(), dto.getProductPrice())); product.setProductPrice(dto.getProductPrice()); }
                    if (product.getProductQuantity() != dto.getProductQuantity()) { changes.append(String.format("Qty (%d -> %d), ", product.getProductQuantity(), dto.getProductQuantity())); product.setProductQuantity(dto.getProductQuantity()); }
                    if (!Objects.equals(product.getType(), dto.getType())) { changes.append(String.format("Type ('%s' -> '%s'), ", product.getType(), dto.getType())); product.setType(dto.getType()); }
                    if (product.getStatus() != dto.getStatus()) { changes.append(String.format("Status (%s -> %s), ", product.getStatus(), dto.getStatus())); product.setStatus(dto.getStatus()); }

                    if (!changes.isEmpty()) {
                        productsToSave.add(product);
                        updatedDetails.add(product.getProductName() + ": " + changes.substring(0, changes.length() - 2));
                    }
                }
            }
        }

        for (ProductExcelDTO dto : creations) {
            if (dto.getProductSku() == null || dto.getProductSku().isBlank()) {
                errors.add("Could not create product '" + dto.getProductName() + "': SKU cannot be empty.");
                continue;
            }
            if (productRepository.findByProductSku(dto.getProductSku()).isPresent()) {
                errors.add("Could not create product '" + dto.getProductName() + "': SKU '" + dto.getProductSku() + "' already exists.");
                continue;
            }
            Product product = new Product();
            product.setProductName(dto.getProductName());
            product.setProductSku(dto.getProductSku());
            product.setProductPrice(dto.getProductPrice());
            product.setProductQuantity(dto.getProductQuantity());
            product.setType(dto.getType());
            product.setStatus(dto.getStatus());
            productsToSave.add(product);
            createdProductNames.add(product.getProductName());
        }

        if (!productsToSave.isEmpty()) {
            log.info("Saving {} updated/created products to the database.", productsToSave.size());
            productRepository.saveAll(productsToSave);
        }

        return new BulkUpdateResultDTO(updatedDetails, createdProductNames, errors);
    }

    private ProductExcelDTO convertToDto(Product product) {
        ProductExcelDTO dto = new ProductExcelDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductSku(product.getProductSku());
        dto.setProductPrice(product.getProductPrice());
        dto.setProductQuantity(product.getProductQuantity());
        dto.setType(product.getType());
        dto.setStatus(product.getStatus());
        return dto;
    }
}