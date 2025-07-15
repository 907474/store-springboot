package com.myapp.aw.store.service.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.myapp.aw.store.model.dto.ProductExcelDTO;

import java.util.ArrayList;
import java.util.List;

// This listener now only reads data into a list.
public class ProductDataListener extends AnalysisEventListener<ProductExcelDTO> {

    private final List<ProductExcelDTO> list = new ArrayList<>();

    public List<ProductExcelDTO> getReadData() {
        return list;
    }

    @Override
    public void invoke(ProductExcelDTO data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // No database logic here anymore.
    }
}