package driver;

import com.epam.healenium.SelfHealingDriver;
import io.appium.java_client.android.AndroidDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.TestRunConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;


public class DriverFactory {
    private static ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (webDriver.get() == null) {
            webDriver.set(createDriver());
        }
        return webDriver.get();
    }

    private static WebDriver createDriver() {
        WebDriver driver = null;
        AndroidDriver androidDriver = null;
        switch (getBrowserType()) {
            case "chrome" -> {
                try {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    WebDriverManager.chromedriver().setup();
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                   // driver = new ChromeDriver(chromeOptions);
                    driver = SelfHealingDriver.create((WebDriver) chromeOptions);
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
                    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                    driver.manage().window().maximize();
//                    driver.findElement(By.xpath(""));
//                    driver.close();
                }catch(WebDriverException e){
                    e.printStackTrace();
                }
                break;
            }
            case "firefox" -> {
                try {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    driver = new FirefoxDriver(firefoxOptions);
                    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                    driver.manage().window().maximize();
                }catch(WebDriverException e){
                    e.printStackTrace();
                }
                break;
            }
            case "headlesschrome" -> {
                ChromeOptions options = new ChromeOptions();
                WebDriverManager.chromedriver().setup();
                options.addArguments("headless");
                driver = new ChromeDriver(options);
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            }
            case "bsandroid" -> {
                try {
                    final String AUTOMATE_USERNAME = TestRunConfig.BS_USERNAME;
                    final String AUTOMATE_ACCESS_KEY = TestRunConfig.BS_ACCESSKEY;
                    final String URL = "https://" + AUTOMATE_USERNAME + ":" + AUTOMATE_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
                    DesiredCapabilities caps = new DesiredCapabilities();
                    caps.setCapability("browserName", TestRunConfig.BS_BROWSERNAME);
                    caps.setCapability("deviceName", TestRunConfig.BS_DEVICE);
                    caps.setCapability("realMobile", "true");
                    caps.setCapability("os_version", TestRunConfig.BS_OSVERSION);
                    caps.setCapability("name", "BStack-[Java] Sample Test"); // test name
                    caps.setCapability("build", "BStack Build Number Test");
                    caps.setCapability("app", "bs://f17a7b20dd5f3807b469c0952e4f8257474462fa");
                    androidDriver = new AndroidDriver(new URL(URL), caps);

                }catch(MalformedURLException e){
                    e.printStackTrace();
                }
                break;
            }
            case "bschromebrowser" -> {
                try{
                    final String USERNAME = TestRunConfig.BS_USERNAME;;
                    final String AUTOMATE_KEY = TestRunConfig.BS_ACCESSKEY;
                    final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
                        DesiredCapabilities caps = new DesiredCapabilities();

                        caps.setCapability("os", "Windows");
                        caps.setCapability("os_version", "10");
                        caps.setCapability("browser", "Chrome");
                        caps.setCapability("browser_version", "80");

                        caps.setCapability("name", "BrowserStack's First Test");

                        driver = new RemoteWebDriver(new URL(URL), caps);
                        driver.get("http://www.google.com");
                        WebElement element = driver.findElement(By.name("q"));
                        element.sendKeys("BrowserStack");
                        element.submit();

                        System.out.println(driver.getTitle());
                       // driver.quit();
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }
            }
            case "remotebrowserchrome" -> {
                try {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setCapability("browserName", "chrome");
//                    chromeOptions.setCapability("browserVersion", "104");
                    chromeOptions.setCapability("platformName", "Windows");
                    chromeOptions.setCapability("se:name", "My simple test");
                    chromeOptions.setCapability("se:sampleMetadata", "Sample metadata value");
                    driver = new RemoteWebDriver(new URL("http://192.168.5.103:4444"), chromeOptions);
                    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                    driver.manage().window().maximize();
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }
            }
        }

        if(driver != null)
            return driver;
        else if(androidDriver != null)
            return androidDriver;
        else
            throw new WebDriverException("No Driver object is created");
    }

    private static String getBrowserType() {
        String browserType = null;

        try {
            Properties properties = new Properties();
            FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/properties/config.properties");
            properties.load(file);
            browserType = properties.getProperty("browser").toLowerCase().trim();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return browserType;
    }

    public static void cleanupDriver() {
        webDriver.get().quit();
        webDriver.remove();
    }
}
