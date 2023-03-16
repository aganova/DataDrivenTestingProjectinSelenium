package Utils;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

public class DataHandler {

    private static XSSFSheet excelWSheet;

    public static XSSFSheet readExcel(String path, String sheetName) {
        try {
            // Open the excel file
            FileInputStream excelFile = new FileInputStream(path);
            // Access the required test data sheet
            XSSFWorkbook excelWorkbook = new XSSFWorkbook(excelFile);
            excelWSheet = excelWorkbook.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excel file path: " + path + " --> Is the excel file of given name missing?");
        }
        return excelWSheet;
    }
}
