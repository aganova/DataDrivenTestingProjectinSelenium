package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    @FindBy(className = "woocommerce-store-notice__dismiss-link") private WebElement dismissLink;
    @FindBy(id = "username") private WebElement userNameInput;
    @FindBy(id = "password") private WebElement passwordInput;
    @FindBy(css = "button[name='login'") private WebElement loginButton;
    @FindBy(css = "div.woocommerce > ul[role='alert']") private WebElement loginFailedAlert;
    private WebDriver driver;
    private WebDriverWait wait;
    private String url = "https://fakestore.testelka.pl/moje-konto/";

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public LoginPage openPage() {
        driver.navigate().to(url);
        return this;
    }

    public void demoDismissNotice() {
        wait.until(ExpectedConditions.elementToBeClickable(dismissLink));
        dismissLink.click();
    }

    public LoginPage sendUsername(String username) {
        userNameInput.sendKeys(username);
        return this;
    }

    public LoginPage sendPassword(String password) {
        passwordInput.sendKeys(password);
        return this;
    }

    public void login() {
        loginButton.click();
    }

    public void clearLoginForm() {
        userNameInput.clear();
        passwordInput.clear();
    }

    public String getErrorAlertMessage() {
        String message = wait.until(ExpectedConditions.visibilityOf(loginFailedAlert)).getText();
        return message;
    }
}
