package ar.com.clevcore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public final class OfficeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(OfficeUtils.class);

    private OfficeUtils() {
        throw new AssertionError();
    }

    public static String getExcel(List<Object> objectList, List<String> propertyList, String path,
            boolean isNewFormatExcel, String patternDate) {
        try {
            Row row;
            Cell cell;
            int rowIndex = 0;
            short columnIndex;

            if (propertyList == null || propertyList.isEmpty()) {
                propertyList = Utils.getPropertiesFromObject(objectList.get(0).getClass());
            } else {
                Utils.prepareProperties(propertyList, objectList.get(0).getClass());
            }

            String name = objectList.get(0).getClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);

            Workbook workbook = isNewFormatExcel ? new XSSFWorkbook() : new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(name.length() > 25 ? name.substring(0, 22) + "..." : name);

            // style
            CellStyle csTitle = getCellStyleTitle(workbook);
            CellStyle csHead = getCellStyleHead(workbook);
            CellStyle csRow = getCellStyleRow(workbook);
            CellStyle csRowDate = getCellStyleRowDate(workbook, patternDate);
            CellStyle csRowDouble = getCellStyleRowDouble(workbook);

            // title
            row = sheet.createRow(rowIndex++);
            cell = row.createCell(0);
            cell.setCellStyle(csTitle);
            cell.setCellValue(name);
            rowIndex++;

            // head
            columnIndex = 0;
            row = sheet.createRow(rowIndex++);
            for (String property : propertyList) {
                cell = row.createCell(columnIndex++);
                cell.setCellStyle(csHead);
                cell.setCellValue(property);
            }

            // body
            for (Object object : objectList) {
                columnIndex = 0;
                row = sheet.createRow(rowIndex++);
                for (String property : propertyList) {
                        cell = row.createCell(columnIndex++);
                        setCellValueSetter(cell, Utils.getValueFromProperty(object, property), csRow, csRowDate,
                                csRowDouble);
                }
            }

            // auto size column
            for (int column = 0; column < row.getLastCellNum(); column++) {
                sheet.autoSizeColumn(column);
            }

            // file
            File file = File.createTempFile(name, ".xls" + (isNewFormatExcel ? "x" : ""), new File(path));
            file.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

            return file.getName();
        } catch (IOException e) {
            LOG.error("[E] IOException occurred in [getExcel]", e);
        }
        return null;
    }

    @SuppressWarnings("resource")
    public static String getPdf(List<Object> objectList, List<String> propertyList, String path, boolean newFormatExcel,
            String patternDate) {
        try {
            String excelFile = getExcel(objectList, propertyList, path, newFormatExcel, patternDate);
            String pdfFile = excelFile.substring(0, excelFile.lastIndexOf(".")) + ".pdf";

            FileInputStream input_document = new FileInputStream(path + excelFile);
            Workbook workbook = (newFormatExcel ? new XSSFWorkbook(input_document) : new HSSFWorkbook(input_document));
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Document pdf = new Document();
            PdfWriter.getInstance(pdf, new FileOutputStream(path + pdfFile));
            pdf.open();

            PdfPTable pdfTable = new PdfPTable(2);
            PdfPCell pdfCell;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        pdfCell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                        pdfTable.addCell(pdfCell);
                    }
                }
            }

            pdf.add(pdfTable);
            pdf.close();
            input_document.close();

            return pdfFile;
        } catch (IOException | DocumentException e) {
            LOG.error("[E] IOException occurred in [getPdf]", e);
        }
        return null;
    }

    // HELPER
    private static void setCellValueSetter(Cell cell, Object value, CellStyle csRow, CellStyle csRowDate,
            CellStyle csRowDouble) {
        if (value != null) {
            if (Date.class.equals(value.getClass())) {
                cell.setCellStyle(csRowDate);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue((Date) value);
            } else if (Double.class.equals(value.getClass())) {
                cell.setCellStyle(csRowDouble);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(((Double) value));
            } else if (Integer.class.equals(value.getClass()) || Long.class.equals(value.getClass())
                    || BigInteger.class.equals(value.getClass())) {
                cell.setCellStyle(csRow);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                if (Integer.class.equals(value.getClass())) {
                    cell.setCellValue((Integer) value);
                } else if (Long.class.equals(value.getClass())) {
                    cell.setCellValue((Long) value);
                } else if (BigInteger.class.equals(value.getClass())) {
                    cell.setCellValue((RichTextString) value);
                }
            } else {
                cell.setCellStyle(csRow);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(value.toString());
            }
        } else {
            cell.setCellStyle(csRow);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue("");
        }
    }

    private static CellStyle setBorder(CellStyle cs) {
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cs;
    }

    private static CellStyle getCellStyleTitle(Workbook workbook) {
        Font font;
        CellStyle cs;

        font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        cs = workbook.createCellStyle();
        cs.setFont(font);

        return cs;
    }

    private static CellStyle getCellStyleHead(Workbook workbook) {
        Font font;
        CellStyle cs;

        font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 10);

        cs = workbook.createCellStyle();
        cs = setBorder(cs);
        cs.setFont(font);
        cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setFillPattern((short) CellStyle.SOLID_FOREGROUND);
        cs.setWrapText(false);

        return cs;
    }

    private static CellStyle getCellStyleRow(Workbook workbook) {
        Font font;
        CellStyle cs;

        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        cs = workbook.createCellStyle();
        cs = setBorder(cs);
        cs.setFont(font);
        cs.setWrapText(true);

        return cs;
    }

    private static CellStyle getCellStyleRowDate(Workbook workbook, String patternDate) {
        DataFormat format;
        Font font;
        CellStyle cs;

        format = workbook.createDataFormat();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        cs = workbook.createCellStyle();
        cs = setBorder(cs);
        cs.setFont(font);
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setWrapText(false);
        cs.setDataFormat(format.getFormat(patternDate));

        return cs;
    }

    private static CellStyle getCellStyleRowDouble(Workbook workbook) {
        DataFormat format;
        Font font;
        CellStyle cs;

        format = workbook.createDataFormat();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        cs = workbook.createCellStyle();
        cs = setBorder(cs);
        cs.setFont(font);
        cs.setAlignment(CellStyle.ALIGN_RIGHT);
        cs.setWrapText(false);
        cs.setDataFormat(format.getFormat("#,##0.00#####"));

        return cs;
    }

}
