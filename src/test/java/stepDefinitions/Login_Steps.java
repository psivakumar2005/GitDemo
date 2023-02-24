package stepDefinitions;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pageObjects.Base_PO;
import pageObjects.Login_PO;
import utils.TestContext;
import utils.TestRunConfig;
import utils.dbOperations.QueryTestDataExcel;

import java.util.List;
import java.util.Map;


public class Login_Steps extends Base_PO {
    private static final Logger log = LoggerFactory.getLogger(Login_Steps.class);
    TestContext testContext;
    private Login_PO login_po;
    QueryTestDataExcel queryTestDataExcel;

    public Login_Steps(Login_PO login_po, QueryTestDataExcel queryTestDataExcel,TestContext testContext) {

        this.login_po = login_po;
        this.queryTestDataExcel = queryTestDataExcel;
        this.testContext=testContext;
    }

    @Given("I access the webdriver university login page")
    public void i_access_the_webdriver_university_login_page() {

        login_po.navigateTo_WebDriverUniversity_Login_Page();
    }

    @When("I enter a username {}")
    public void i_enter_a_username(String username) {

        testContext.setPolicy(login_po.setUsername(username));
    }

    @And("I enter a password {}")
    public void i_enter_a_password(String password) {
        login_po.setPassword(password);
    }

    @And("I click on the login button")
    public void i_click_on_the_login_button() {
        login_po.clickOn_Login_Button();
    }

    @Then("I should be presented with the successful login message")
    public void i_should_be_presented_with_the_successful_login_message() {
        login_po.validate_SuccessfulLogin_Message();
    }

    @Then("I should be presented with the unsuccessful login message")
    public void i_should_be_presented_with_the_unsuccessful_login_message() {
        login_po.validate_UnsuccessfulLogin_Message();
    }

    @Then("I should be presented with the following login validation message {}")
    public void i_should_be_presented_with_the_following_login_validation_message(String expectedMessage) {
        waitForAlert_And_ValidateText(expectedMessage);
    }

    @And("user logs into WebDriver University Application")
    public void userLogsIntoWebDriverUniversityApplication(List<User> users) {
        User user = users.get(0);
        user.setEnvironment(TestRunConfig.ENV);
        user = queryTestDataExcel.retrieveUserObj(user);
        login_po.logsIntoApplication(user);
    }

    @DataTableType
    public User convert(Map<String, String> entry) {
        return new User(
                entry.get("User Role")
        );
    }

}