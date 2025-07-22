package ae.cyfr.estimateapp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import ae.cyfr.estimateapp.model.Estimate;
import ae.cyfr.estimateapp.repository.EstimateRepository;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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

        // Шрифты
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 16);

        org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short) 14);

        // Стиль заголовков
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Стиль обычных ячеек
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Стиль для наименования работ (выравнивание по левому краю)
        CellStyle nameColumnDataStyle = workbook.createCellStyle();
        nameColumnDataStyle.setFont(dataFont);
        nameColumnDataStyle.setAlignment(HorizontalAlignment.LEFT);
        nameColumnDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        nameColumnDataStyle.setBorderBottom(BorderStyle.THIN);
        nameColumnDataStyle.setBorderTop(BorderStyle.THIN);
        nameColumnDataStyle.setBorderLeft(BorderStyle.THIN);
        nameColumnDataStyle.setBorderRight(BorderStyle.THIN);

        // Стиль числовых ячеек
        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setFont(dataFont);
        numericStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        numericStyle.setAlignment(HorizontalAlignment.CENTER);
        numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numericStyle.setBorderBottom(BorderStyle.THIN);
        numericStyle.setBorderTop(BorderStyle.THIN);
        numericStyle.setBorderLeft(BorderStyle.THIN);
        numericStyle.setBorderRight(BorderStyle.THIN);

        // Заголовки
        String[] headers = {"Наименование работ", "Количество", "Стоимость за ед., AED", "Итого, AED"};
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(30);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Данные
        int rowNum = 1;
        double grandTotal = 0.0;

        for (Estimate item : estimates) {
            Row row = sheet.createRow(rowNum++);

            // 1. Наименование работ
            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(item.getWork().getName());
            nameCell.setCellStyle(nameColumnDataStyle);

            // 2. Количество + единица
            Cell quantityCell = row.createCell(1);
            String quantityWithUnit = String.format("%.2f %s", item.getQuantity(), item.getWork().getUnit());
            quantityCell.setCellValue(quantityWithUnit);
            quantityCell.setCellStyle(dataStyle);

            // 3. Стоимость за единицу
            Cell priceCell = row.createCell(2);
            double clientPriceWithCoeff = item.getWork().getClientPrice() * item.getCoefficient();
            priceCell.setCellValue(clientPriceWithCoeff);
            priceCell.setCellStyle(numericStyle);

            // 4. Итоговая сумма
            double total = item.getTotalCost();
            grandTotal += total;
            Cell totalCell = row.createCell(3);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(numericStyle);
        }

        // Автоподбор ширины колонок
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, (int) (currentWidth * 1.1));
        }

        // Итоговая строка
        Row totalRow = sheet.createRow(rowNum + 1);
        Cell labelCell = totalRow.createCell(2);
        labelCell.setCellValue("Итоговая сумма, AED:");
        labelCell.setCellStyle(headerStyle);

        Cell valueCell = totalRow.createCell(3);
        valueCell.setCellValue(grandTotal);
        valueCell.setCellStyle(numericStyle);

        // Запись
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
