package Excel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelLib {
    private static final DataFormatter dataFormatter = new DataFormatter();

    private static Logger logger = LogManager.getLogger();

    private static Workbook getWorkbook(String fileName) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileName);
        return new XSSFWorkbook(inputStream);
    }

    private static Sheet getSheet(String fileName, String sheetName) throws IOException {
        Workbook workbook = getWorkbook(fileName);
        return workbook.getSheet(sheetName);
    }

    public static List<Excel.DataCollection> populateInCollection(String fileName, String sheetName) throws IOException {
        List<Excel.DataCollection> dataCol = new ArrayList<>();
        dataCol.clear();
        Sheet sheet = getSheet(fileName, sheetName);

        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
            Row row = sheet.getRow(rowNumber);
            if (row != null) {
                for (int col = 0; col < row.getLastCellNum(); col++) {
                    Excel.DataCollection dtTable = new Excel.DataCollection();
                    dtTable.setTotalRowCount(sheet.getLastRowNum());
                    dtTable.setRowNumber(rowNumber);
                    dtTable.setColName(sheet.getRow(0).getCell(col).getStringCellValue());
                    dtTable.setColValue(dataFormatter.formatCellValue(row.getCell(col)));
                    dataCol.add(dtTable);
                }
            }
        }
        return dataCol;
    }

    public static String readData(List<DataCollection> dataCol, int rowNumber, String columnName) {
        try {
            for (DataCollection colData : dataCol) {
                if (colData.getColName().equals(columnName) && colData.getRowNumber() == rowNumber) {
                    String data = colData.getColValue();
                    if (data == null || data.length() == 0) {
                        return null;
                    }
                    return data;
                }
            }
            return null;
        } catch (Exception e) {
            logger.info(e.getMessage() + " For " + columnName + " at " + rowNumber);
            return null;
        }
    }

    public static List<String> populateInCollectionRows(String fileName, String sheetName) throws IOException {
        List<String> allRowsRecord = new ArrayList<>();
        Sheet sheet = getSheet(fileName, sheetName);

        for (int row = 0; row <= sheet.getLastRowNum(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow != null) {
                List<String> rowRecord = new ArrayList<>();
                for (int col = 0; col < currentRow.getLastCellNum(); col++) {
                    rowRecord.add(dataFormatter.formatCellValue(currentRow.getCell(col)));
                }
                allRowsRecord.add(String.join(" , ", rowRecord));
            }
        }
        return allRowsRecord;
    }
}

