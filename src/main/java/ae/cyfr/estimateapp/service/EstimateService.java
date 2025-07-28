package ae.cyfr.estimateapp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
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

        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 12);

        org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
        dataFont.setFontName("Times New Roman");
        dataFont.setFontHeightInPoints((short) 10);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        CellStyle nameColumnDataStyle = workbook.createCellStyle();
        nameColumnDataStyle.setFont(dataFont);
        nameColumnDataStyle.setAlignment(HorizontalAlignment.LEFT);
        nameColumnDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        nameColumnDataStyle.setBorderBottom(BorderStyle.THIN);
        nameColumnDataStyle.setBorderTop(BorderStyle.THIN);
        nameColumnDataStyle.setBorderLeft(BorderStyle.THIN);
        nameColumnDataStyle.setBorderRight(BorderStyle.THIN);

        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setFont(dataFont);
        numericStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        numericStyle.setAlignment(HorizontalAlignment.CENTER);
        numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numericStyle.setBorderBottom(BorderStyle.THIN);
        numericStyle.setBorderTop(BorderStyle.THIN);
        numericStyle.setBorderLeft(BorderStyle.THIN);
        numericStyle.setBorderRight(BorderStyle.THIN);

        String[] headers = {"Наименование работ", "Количество", "Стоимость за ед., AED", "Итого, AED"};
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(30);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        double grandTotal = 0.0;

        for (Estimate item : estimates) {
            Row row = sheet.createRow(rowNum++);

            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(item.getWork().getName());
            nameCell.setCellStyle(nameColumnDataStyle);

            Cell quantityCell = row.createCell(1);
            String quantityWithUnit = String.format("%.2f %s", item.getQuantity(), item.getWork().getUnit());
            quantityCell.setCellValue(quantityWithUnit);
            quantityCell.setCellStyle(dataStyle);

            Cell priceCell = row.createCell(2);
            double clientPriceWithCoeff = item.getWork().getClientPrice() * item.getCoefficient();
            priceCell.setCellValue(clientPriceWithCoeff);
            priceCell.setCellStyle(numericStyle);

            double total = item.getTotalCost();
            grandTotal += total;
            Cell totalCell = row.createCell(3);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(numericStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, (int) (currentWidth * 1.1));
        }

        Row totalRow = sheet.createRow(rowNum + 1);
        Cell labelCell = totalRow.createCell(2);
        labelCell.setCellValue("Итоговая сумма, AED:");
        labelCell.setCellStyle(headerStyle);

        Cell valueCell = totalRow.createCell(3);
        valueCell.setCellValue(grandTotal);
        valueCell.setCellStyle(numericStyle);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    public byte[] createEstimateWord(List<Estimate> estimates) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            title.setSpacingBefore(120);
            title.setSpacingAfter(120);

            XWPFRun titleRun = title.createRun();
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            titleRun.setText("Смета от " + currentDate);
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setFontFamily("Times New Roman");
            titleRun.addBreak();

            XWPFTable table = document.createTable();
            CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            tblWidth.setType(STTblWidth.PCT);
            tblWidth.setW(BigInteger.valueOf(5000));
            table.getCTTbl().getTblPr().addNewTblLayout().setType(STTblLayoutType.AUTOFIT);

            XWPFTableRow header = table.getRow(0);
            createWordCell(header, 0, "Наименование работы", true, "50%");
            createWordCell(header, 1, "Количество", true, "15%");
            createWordCell(header, 2, "Цена, AED", true, "15%");
            createWordCell(header, 4, "Итого, AED", true, "20%");

            double totalSum = 0;
            DecimalFormat df = new DecimalFormat("#,##0.00");

            for (Estimate item : estimates) {
                XWPFTableRow row = table.createRow();
                createWordCell(row, 0, item.getWork().getName(), false, null);

                String quantityUnitText = df.format(item.getQuantity()) + " " + item.getWork().getUnit();
                createWordCell(row, 1, quantityUnitText, false, null);
                createWordCell(row, 2, df.format(item.getWork().getClientPrice()), false, null);
                createWordCell(row, 3, df.format(item.getTotalCost()), false, null);
                totalSum += item.getTotalCost();
            }

            XWPFParagraph total = document.createParagraph();
            total.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun totalRun = total.createRun();
            totalRun.addBreak();
            totalRun.setText("Общая сумма, AED: " + df.format(totalSum));
            totalRun.setBold(true);
            totalRun.setFontSize(10);
            totalRun.setFontFamily("Times New Roman");

            document.write(out);
            return out.toByteArray();
        }
    }

    private void createWordCell(XWPFTableRow row, int cellIndex, String text, boolean isBold, String width) {
        XWPFTableCell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.addNewTableCell();
        }

        XWPFParagraph paragraph = cell.getParagraphs().get(0);

        while (paragraph.getRuns().size() > 0) {
            paragraph.removeRun(0);
        }

        paragraph.setAlignment(cellIndex == 0 ? ParagraphAlignment.LEFT : ParagraphAlignment.CENTER);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(10);
        run.setFontFamily("Times New Roman");

        if (width != null) {
            CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
            cellWidth.setType(STTblWidth.DXA);
            cellWidth.setW(new BigInteger(String.valueOf(Integer.parseInt(width.replace("%", "")) * 50)));
        }
    }
}