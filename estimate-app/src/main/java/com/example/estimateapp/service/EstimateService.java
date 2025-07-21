package com.example.estimateapp.service;

import com.example.estimateapp.model.EstimateItem;
import com.example.estimateapp.repository.EstimateItemRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class EstimateService {

    @Autowired
    private EstimateItemRepository estimateItemRepository;

    public EstimateItem saveEstimateItem(EstimateItem estimateItem) {
        return estimateItemRepository.save(estimateItem);
    }

    public List<EstimateItem> getAllEstimateItems() {
        return estimateItemRepository.findAll();
    }

    public byte[] createEstimateExcel(List<EstimateItem> estimateItems) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estimate");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Work Name", "Unit", "Quantity", "Customer Price", "Coefficient", "Total"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data
        int rowNum = 1;
        for (EstimateItem item : estimateItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getWork().getName());
            row.createCell(1).setCellValue(item.getWork().getUnit());
            row.createCell(2).setCellValue(item.getQuantity());
            row.createCell(3).setCellValue(item.getWork().getCustomerPrice());
            row.createCell(4).setCellValue(item.getCoefficient());
            row.createCell(5).setCellValue(item.getQuantity() * item.getWork().getCustomerPrice() * item.getCoefficient());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
