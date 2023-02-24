package pageObjects;

import model.Policy;
import model.User;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Global_Vars;
import utils.TestRunConfig;

public class Login_PO extends Base_PO {
    Logger log = LoggerFactory.getLogger(Login_PO.class);
    private @FindBy(id = "text")
    WebElement username_TextField;

    private @FindBy(id = "password")
    WebElement password_TextField;

    private @FindBy(id = "login-button")
    WebElement login_Button;

    public Login_PO() {

        super();
    }

    public void navigateTo_WebDriverUniversity_Login_Page() {
        navigateTo_URL(Global_Vars.WEBDRIVER_UNIVERSITY_HOMEPAGE_URL + "/Login-Portal/index.html?");
    }

    public Policy setUsername(String username) {
        Policy policy = new Policy();
        sendKeys(username_TextField, username);
        policy.setPolicyNumber(username);
        return policy;
    }

    public void setPassword(String password) {
        sendKeys(password_TextField, password);
    }

    public void clickOn_Login_Button() {
        waitForWebElementAndClick(login_Button);
    }

    public void validate_SuccessfulLogin_Message() {
        waitForAlert_And_ValidateText("validation succeeded");
    }

    public void validate_UnsuccessfulLogin_Message() {
        waitForAlert_And_ValidateText("validation failed");
    }

    public void logsIntoApplication (User user) {
        navigateTo_URL(TestRunConfig.BASE_URL);
        setUsername(user.getUserId());
        setPassword(user.getPassword());
        clickOn_Login_Button();
        }

}
