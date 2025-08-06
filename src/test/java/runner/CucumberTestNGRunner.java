package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import mocks.WireMockServerSetup;

@CucumberOptions(
    features = "src/test/java/features",
    glue = {"steps"},
    plugin = {"pretty", "summary", "html:target/cucumber-report.html", "json:target/cucumber-report.json", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
public class CucumberTestNGRunner extends AbstractTestNGCucumberTests {
    @BeforeClass
    public void setUp() {
        WireMockServerSetup.startServer();
    }

    @AfterClass
    public void tearDown() {
        WireMockServerSetup.stopServer();
    }
}
