package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import mocks.WireMockServerSetup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
    features = "src/test/java/features",
    glue = {"stepdefs"},
    plugin = {"pretty", "summary", "html:target/cucumber-report.html", "json:target/cucumber-report.json"}
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
