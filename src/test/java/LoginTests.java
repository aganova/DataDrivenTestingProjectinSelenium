import PageObjects.LoginPage;
import Utils.DataHandler;
import dev.failsafe.internal.util.Assert;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.Random;

public class LoginTests {
    private WebDriver driver;
    private LoginPage loginPage;
    private String folderPath = "src\\test\\java\\TestData\\";
    private XSSFSheet excelSheet;
    private String excelFileName = "UsersData.xlsx";
    private String sheetName = "UsernameCredentials";

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        /* due to bug it's needed to add argument as below to perform testing on Chrome v.111 */
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);

        loginPage = PageFactory.initElements(driver, LoginPage.class);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        loginPage.openPage().demoDismissNotice();
    }

    @Test
    public void loginWithUsername_multipleUnregisteredUsers_shouldFail() {

        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetName);
        int rowsNum = excelSheet.getPhysicalNumberOfRows();

        for (int i = 0; i < rowsNum - 1; i++) {
            String userName = excelSheet.getRow(i + 1).getCell(0).getStringCellValue();
            String userPass = excelSheet.getRow(i + 1).getCell(1).getStringCellValue();
            loginPage.sendUsername(userName).sendPassword(userPass).login();

            Assertions.assertEquals(loginPage.getErrorAlertMessage(), "Błąd: Brak "
                            + userName
                            + " "
                            + "wśród zarejestrowanych w witrynie użytkowników. Jeśli nie masz pewności co do nazwy użytkownika, użyj adresu e-mail.",
                    "Error message is incorrect");

            loginPage.clearLoginForm();
        }
    }

    @Test
    public void loginWithUserEmail_multipleUnregisteredUsers_shouldFail() {

        sheetName = "UserEmailCredentials";
        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetName);
        int rowsNum = excelSheet.getPhysicalNumberOfRows();

        for (int i = 0; i < rowsNum - 1; i++) {
            String userEmail = excelSheet.getRow(i + 1).getCell(0).getStringCellValue();
            String userPass = excelSheet.getRow(i + 1).getCell(1).getStringCellValue();
            loginPage.sendUsername(userEmail).sendPassword(userPass).login();

            Assertions.assertEquals(loginPage.getErrorAlertMessage(),
                    "Nieznany adres e-mail. Proszę sprawdzić ponownie lub wypróbować swoją nazwę użytkownika.",
                    "Error message is incorrect");

            loginPage.clearLoginForm();
        }
    }

    @Test
    public void login_randomUser_NoUserNameGiven_shouldFail() {

        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetName);
        int rowsNum = excelSheet.getPhysicalNumberOfRows();
        //System.out.println(rowsNum);

        String userPass = excelSheet.getRow(DataHandler.getRandomNumber(1, rowsNum)).getCell(1).getStringCellValue();
        loginPage.sendPassword(userPass).login();

        Assertions.assertEquals(loginPage.getErrorAlertMessage(), "Błąd: Nazwa użytkownika jest wymagana.");

        loginPage.clearLoginForm();
    }

    @Test
    public void login_randomUser_NoPasswordGiven_shouldFail() {

        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetName);
        int rowsNum = excelSheet.getPhysicalNumberOfRows();

        String userName = excelSheet.getRow(DataHandler.getRandomNumber(1, rowsNum)).getCell(0).getStringCellValue();
        loginPage.sendUsername(userName).login();

        Assertions.assertEquals(loginPage.getErrorAlertMessage(), "Błąd: Hasło jest puste.");

        loginPage.clearLoginForm();
    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
