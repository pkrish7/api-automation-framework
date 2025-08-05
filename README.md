# API Automation Framework

## Overview
This project is a sample API automation framework using Java, TestNG, Cucumber (BDD), RestAssured, WireMock, SLF4J (with Lombok), and OpenCSV. It demonstrates data-driven testing, externalized configuration, and BDD-style test design for CRUD operations on the `/employees` endpoint.

## Prerequisites
- Java 17+
- Maven 3.6+
- Git

## Setup

1. **Clone the repository**
   ```sh
   git clone https://github.com/pkrish7/api-automation-framework.git
   cd api-automation-framework
   ```

2. **Install dependencies**
   ```sh
   mvn clean install
   ```

3. **Project structure**
   - `src/test/java/stepdefs/EmployeeStepDefs.java`: Step definitions for Cucumber scenarios.
   - `src/test/java/config/TestConfig.java`: Loads config from `src/test/resources/config.properties`.
   - `src/test/java/utils/CsvUtils.java`: Utility for reading CSV test data.
   - `src/test/resources/config.properties`: Externalized configuration (e.g., base URL).
   - `src/test/resources/testdata/employees.csv`: Sample test data for data-driven tests.
   - `src/test/java/features/`: Cucumber feature files.
   - `src/test/java/mocks/WireMockServerSetup.java`: WireMock stubs for API endpoints.

## Configuration

Edit `src/test/resources/config.properties` to change the base URL or other settings:
```
base.url=http://localhost:9090
api.version=v1
```

## Running Tests

1. **Run all tests with Maven (parallel, using testng.xml)**
   ```sh
   mvn test -DsuiteXmlFile=testng.xml
   ```

2. **Run tests by category**
   - Smoke tests:
     ```sh
     mvn test -Dcucumber.options="--tags @smoke"
     ```
   - Regression tests:
     ```sh
     mvn test -Dcucumber.options="--tags @regression"
     ```
   - Negative tests:
     ```sh
     mvn test -Dcucumber.options="--tags @negative"
     ```

3. **Run specific TestNG/Cucumber runner**
   ```sh
   mvn test -Dcucumber.options="--tags @positive"
   ```

4. **View reports**
   - TestNG default reports: `target/surefire-reports`
   - Cucumber HTML report: `target/cucumber-report.html`
   - Allure reports (if enabled): `allure-results/`

## Data-Driven Testing

- Employee data for the READ scenario is in `src/test/resources/testdata/employees.csv`.
- You can add more CSV files for other scenarios as needed.

## Logging

- SLF4J (with Lombok @Slf4j) is used for logging.
- Logs are printed to the console during test execution.

## Customization

- Add new feature files in `src/test/java/features/`.
- Add new step definitions in `src/test/java/stepdefs/`.
- Update WireMock stubs in `src/test/java/mocks/WireMockServerSetup.java` for new endpoints or data.

## Troubleshooting

- If you see errors about missing CSV or config files, ensure they are in `src/test/resources` (or subfolders) and Maven copies them to the classpath.
- For port conflicts, change `base.url` in `config.properties` and update WireMock port in `WireMockServerSetup.java`.

## Retry Logic for Flaky Tests

- The framework automatically retries failed tests up to 2 times using TestNG's retry analyzer.
- This helps reduce false negatives due to intermittent issues (e.g., network glitches, timing problems).
- You can adjust the retry count in `src/test/java/utils/RetryFailedTestAnalyzer.java`.

## Parallel Execution

- TestNG is configured for parallel execution with 4 threads in `testng.xml`.
- Cucumber step definitions are thread-safe by design (new instance per scenario).

## Environment-Specific Test Data

- Test data is organized by environment:
  - `src/test/resources/testdata/dev/employees.csv`
  - `src/test/resources/testdata/qa/employees.csv`
- The environment is set in `src/test/resources/config.properties` via the `env` property (e.g., `env=dev`).
- You can override the environment at runtime:
  ```sh
  mvn test -Denv=qa
  ```
- Step definitions automatically load the correct test data for the selected environment.
