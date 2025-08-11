package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import mocks.WireMockServerSetup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "src/test/java/features/UpdateEmployee.feature",
    glue = {"stepdefs"},
    plugin = {"pretty", "summary", "html:target/cucumber-report.html", "json:target/cucumber-report.json", "rerun:target/rerun.txt"}
)
public class UpdateEmployeeCucumberTest extends AbstractTestNGCucumberTests {
    @BeforeClass
    public void setUp() {
        // Only start WireMock if not running under Docker Compose (i.e., if USE_EXTERNAL_WIREMOCK is not set to true)
        String useExternalWireMock = System.getenv("USE_EXTERNAL_WIREMOCK");
        if (useExternalWireMock == null || !useExternalWireMock.equalsIgnoreCase("true")) {
            WireMockServerSetup.startServer();
        }
    }

    @AfterClass
    public void tearDown() {
        // Only stop WireMock if it was started by this process
        String useExternalWireMock = System.getenv("USE_EXTERNAL_WIREMOCK");
        if (useExternalWireMock == null || !useExternalWireMock.equalsIgnoreCase("true")) {
            WireMockServerSetup.stopServer();
        }
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
