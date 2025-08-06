package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "@target/rerun.txt",
    glue = {"stepdefs"},
    plugin = {"pretty", "summary", "html:target/cucumber-rerun-report.html", "json:target/cucumber-rerun-report.json"}
)
public class CucumberRerunTest extends AbstractTestNGCucumberTests {
    // No setup/teardown needed for rerun
}

