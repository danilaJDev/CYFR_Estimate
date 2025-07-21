package ae.cyfr.estimateapp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.repository.EstimateItemRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstimateService {

    @Autowired
    private EstimateItemRepository estimateItemRepository;

    public Estimate saveEstimateItem(Estimate estimateItem) {
        return estimateItemRepository.save(estimateItem);
    }

    public List<Estimate> getAllEstimateItems() {
        return estimateItemRepository.findAll();
    }

    public byte[] createEstimateExcel(List<Estimate> estimateItems) throws IOException {
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
        for (Estimate item : estimateItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getWork().getName());
            row.createCell(1).setCellValue(item.getWork().getUnit());
            row.createCell(2).setCellValue(item.getQuantity());

            BigDecimal clientPrice = item.getWork().getClientPrice();
            row.createCell(3).setCellValue(clientPrice.doubleValue());

            row.createCell(4).setCellValue(item.getCoefficient());

            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal coefficient = BigDecimal.valueOf(item.getCoefficient());
            BigDecimal totalCost = clientPrice.multiply(quantity).multiply(coefficient);

            row.createCell(5).setCellValue(totalCost.doubleValue());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
