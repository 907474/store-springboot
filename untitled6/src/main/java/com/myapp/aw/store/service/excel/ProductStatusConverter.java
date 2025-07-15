package com.myapp.aw.store.service.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.myapp.aw.store.model.ProductStatus;

public class ProductStatusConverter implements Converter<ProductStatus> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return ProductStatus.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * This converts the ProductStatus enum into a String when reading from the database for download.
     */
    @Override
    public WriteCellData<?> convertToExcelData(ProductStatus value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value.name());
    }

    /**
     * This converts a String from an uploaded Excel file back into a ProductStatus enum.
     */
    @Override
    public ProductStatus convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return ProductStatus.valueOf(cellData.getStringValue());
    }
}