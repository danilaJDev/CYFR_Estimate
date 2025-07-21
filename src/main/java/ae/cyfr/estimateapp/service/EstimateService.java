package ae.cyfr.estimateapp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.repository.EstimateRepository;
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
    private EstimateRepository estimateRepository;

    public Estimate saveEstimate(Estimate estimate) {
        return estimateRepository.save(estimate);
    }

    public List<Estimate> getAllEstimates() {
        return estimateRepository.findAll();
    }

    public byte[] createEstimateExcel(List<Estimate> estimates) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estimate");

        // Font settings
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short) 11);

        // Cell styles
        org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        org.apache.poi.ss.usermodel.CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setFont(dataFont);
        numericStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        numericStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        numericStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        numericStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        numericStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Наименование работ", "Единица измерения", "Количество", "Стоимость для заказчика", "Коэффициент", "Итого"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 1;
        for (Estimate item : estimates) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getWork().getName());
            row.getCell(0).setCellStyle(dataStyle);

            row.createCell(1).setCellValue(item.getWork().getUnit());
            row.getCell(1).setCellStyle(dataStyle);

            row.createCell(2).setCellValue(item.getQuantity());
            row.getCell(2).setCellStyle(numericStyle);

            BigDecimal clientPrice = item.getWork().getClientPrice();
            row.createCell(3).setCellValue(clientPrice.doubleValue());
            row.getCell(3).setCellStyle(numericStyle);

            row.createCell(4).setCellValue(item.getCoefficient());
            row.getCell(4).setCellStyle(numericStyle);

            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal coefficient = BigDecimal.valueOf(item.getCoefficient());
            BigDecimal totalCost = clientPrice.multiply(quantity).multiply(coefficient);

            row.createCell(5).setCellValue(totalCost.doubleValue());
            row.getCell(5).setCellStyle(numericStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
