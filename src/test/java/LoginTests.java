import PageObjects.LoginPage;
import Utils.DataHandler;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginTests {
    private WebDriver driver;
    private LoginPage loginPage;
    private XSSFSheet excelSheet;
    private String folderPath = "src\\test\\java\\TestData\\";
    private String excelFileName = "UsersData.xlsx";
    private List<String> sheetNamesList = new ArrayList<>(Arrays.asList("UsernameCredentials", "UserEmailCredentials"));
    private String userName;
    private String userPass;

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
        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetNamesList.get(0));
        int rowsCounter = excelSheet.getPhysicalNumberOfRows();

        for (int i = 0; i < rowsCounter - 1; i++) {
            sendLoginCredentialsFromExcelFile(i + 1, 0);

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
        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetNamesList.get(1));
        int rowsNum = excelSheet.getPhysicalNumberOfRows();

        for (int i = 0; i < rowsNum - 1; i++) {
            sendLoginCredentialsFromExcelFile(i + 1, 0);

            Assertions.assertEquals(loginPage.getErrorAlertMessage(),
                    "Nieznany adres e-mail. Proszę sprawdzić ponownie lub wypróbować swoją nazwę użytkownika.",
                    "Error message is incorrect");
            loginPage.clearLoginForm();
        }
    }

    @Test
    public void login_randomUser_NoUserNameGiven_shouldFail() {

        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetNamesList.get(0));
        int rowsNum = excelSheet.getPhysicalNumberOfRows();
        userPass = excelSheet.getRow(DataHandler.getRandomNumber(1, rowsNum)).getCell(1).getStringCellValue();
        loginPage.sendPassword(userPass).login();

        Assertions.assertEquals(loginPage.getErrorAlertMessage(), "Błąd: Nazwa użytkownika jest wymagana.");
        loginPage.clearLoginForm();
    }

    @Test
    public void login_randomUser_NoPasswordGiven_shouldFail() {
        excelSheet = DataHandler.readExcel(folderPath + excelFileName, sheetNamesList.get(0));
        int rowsNum = excelSheet.getPhysicalNumberOfRows();
        userName = excelSheet.getRow(DataHandler.getRandomNumber(1, rowsNum)).getCell(0).getStringCellValue();
        loginPage.sendUsername(userName).login();

        Assertions.assertEquals(loginPage.getErrorAlertMessage(), "Błąd: Hasło jest puste.");
        loginPage.clearLoginForm();
    }

    private void sendLoginCredentialsFromExcelFile(int rowNum, int cellNum) {
        userName = excelSheet.getRow(rowNum).getCell(cellNum).getStringCellValue();
        userPass = excelSheet.getRow(rowNum).getCell(cellNum + 1).getStringCellValue();
        loginPage.sendUsername(userName).sendPassword(userPass).login();
    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
