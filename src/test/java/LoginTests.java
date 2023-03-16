import PageObjects.LoginPage;
import Utils.DataHandler;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class LoginTests {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        /* due to bug it's needed to add argument as below to perform testing on Chrome v.111 */
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);

        loginPage = PageFactory.initElements(driver, LoginPage.class);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    }

    @Test
    public void login_multipleUsersWithNoAccess_shouldFail() {
        loginPage.openPage().demoDismissNotice();

        XSSFSheet excelSheet = DataHandler.readExcel("src\\test\\java\\TestData\\UsersData.xlsx", "UserCredentials");
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

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
