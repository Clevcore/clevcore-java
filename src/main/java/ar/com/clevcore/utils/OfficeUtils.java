package ar.com.clevcore.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
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

public final class OfficeUtils {

    private OfficeUtils() {
        throw new AssertionError();
    }

    public static File getExcel(List<?> objectList, String filePath) throws IOException {
        return getExcel(objectList, null, null, null, filePath, null, null, null);
    }

    public static File getExcel(List<?> objectList, List<String> propertyList, String filePath) throws IOException {
        return getExcel(objectList, propertyList, null, null, filePath, null, null, null);
    }

    public static File getExcel(List<?> objectList, List<String> propertyList, List<String> headList, String filePath)
            throws IOException {
        return getExcel(objectList, propertyList, headList, null, filePath, null, null, null);
    }

    public static File getExcel(List<?> objectList, List<String> propertyList, List<String> headList, String title,
            String filePath, String fileName, Boolean newfileFormat, String patternDate) throws IOException {
        FileOutputStream output = null;

        Workbook workbook;
        Sheet sheet;
        Row row;
        Cell cell;

        int rowIndex = 0;
        short columnIndex;

        try {
            // init
            if (propertyList == null || propertyList.isEmpty()) {
                propertyList = Utils.getPropertiesFromObject(objectList.get(0).getClass());
            } else {
                Utils.prepareProperties(propertyList, objectList.get(0).getClass());
            }

            if (headList == null) {
                headList = propertyList;
            }

            if (title == null) {
                title = objectList.get(0).getClass().getName();
                title = title.substring(title.lastIndexOf(".") + 1);
            }

            if (fileName == null) {
                fileName = title;
            }

            if (newfileFormat == null) {
                newfileFormat = true;
            }

            if (patternDate == null) {
                patternDate = DateUtils.PATTERN_DATE;
            }

            workbook = newfileFormat ? new XSSFWorkbook() : new HSSFWorkbook();
            sheet = workbook.createSheet();

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
            cell.setCellValue(title);
            rowIndex++;

            // head
            columnIndex = 0;
            row = sheet.createRow(rowIndex++);
            for (String head : headList) {
                cell = row.createCell(columnIndex++);
                cell.setCellStyle(csHead);
                cell.setCellValue(head);
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
            File file = File.createTempFile(fileName, ".xls" + (newfileFormat ? "x" : ""), new File(filePath));
            file.deleteOnExit();

            output = new FileOutputStream(file);

            workbook.write(output);

            return file;
        } finally {
            IOUtils.close(output);
        }
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
