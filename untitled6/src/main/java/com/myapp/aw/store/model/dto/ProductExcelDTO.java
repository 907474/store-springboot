package com.myapp.aw.store.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.myapp.aw.store.model.ProductStatus;
import com.myapp.aw.store.service.excel.ProductStatusConverter; // Import the new converter

import java.math.BigDecimal;

public class ProductExcelDTO {

    @ExcelProperty("Product ID")
    @ColumnWidth(15)
    private Long productId;

    @ExcelProperty("Name")
    @ColumnWidth(30)
    private String productName;

    @ExcelProperty("SKU")
    @ColumnWidth(20)
    private String productSku;

    @ExcelProperty("Price")
    @ColumnWidth(15)
    private BigDecimal productPrice;

    @ExcelProperty("Quantity")
    @ColumnWidth(15)
    private int productQuantity;

    @ExcelProperty("Type")
    @ColumnWidth(20)
    private String type;

    @ExcelProperty(value = "Status", converter = ProductStatusConverter.class)
    @ColumnWidth(20)
    private ProductStatus status;


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}