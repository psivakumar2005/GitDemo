package runners;

import io.cucumber.testng.*;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import utils.Constants;
import utils.RetryAnalyzer;
import utils.TestRunConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Kishor Kumar Kottakota
@CucumberOptions(tags = "@TCLogin1",
        plugin = {"pretty", "html:target/cucumber/report.html",
                "json:target/cucumber/report.json",
                "junit:target/cucumber-results.xml",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"},
        features = "src/test/resources/features",
        glue = {"stepDefinitions"},
        dryRun = false,
        monochrome = true)

public class MainRunner extends AbstractTestNGCucumberTests {

    private static final Logger log = LoggerFactory.getLogger(MainRunner.class);
    private TestNGCucumberRunner testNGCucumberRunner;

    private static final String THREAD_COUNT_KEY = "dataproviderthreadcount";
    private static final String THREAD_COUNT_VALUE = TestRunConfig.THREAD_COUNT;
    private static final boolean isParallel = true;
    private static final String PROJECT_NAME = TestRunConfig.get("projectName");



    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        String filePath = null;
        System.setProperty(THREAD_COUNT_KEY, THREAD_COUNT_VALUE);
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("linux"))
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
        } catch (IOException e) {
            log.info(e.getMessage());
        }

    }

    @Override
    @DataProvider(parallel = isParallel)
    public Object[][] scenarios() {
        if (testNGCucumberRunner == null) {
            testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }
        return testNGCucumberRunner.provideScenarios();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    @AfterClass(alwaysRun = true)
    public void testDownClass() throws IOException {
        if (testNGCucumberRunner == null) {
            return;
        }
        testNGCucumberRunner.finish();
        File cucumberJsonFile = new File("target/cucumber/report.json");
        File encodedStrFile = new File("target/encoded_cucumber_json.txt");
        encodedStrFile.createNewFile();
        File reportOutputDirectory = new File("target");
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("target/cucumber/report.json");

        String projectName = PROJECT_NAME;

        Configuration configuration = new Configuration(reportOutputDirectory, projectName);

        // configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        // configuration.setNotFailingStatuses(Collections.singleton(Status.FAILED));
        // configuration.addClassifications("Tags", "contact-us");
        configuration.addClassifications("Platform", System.getProperty("os.name"));
        configuration.addClassifications("Environment", TestRunConfig.ENV);
        configuration.addClassifications("Browser", TestRunConfig.BROWSER);

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}
