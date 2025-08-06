package stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.TestConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import utils.AssertUtils;
import utils.CsvUtils;
import utils.FileUtils;
import utils.RequestBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmployeeStepDefs {

    private int responseCode;
    private String responseBody;
    private Response response;
    private static final Map<String, String> JSON_HEADERS = Map.of("Content-Type", "application/json");

    // CREATE EMPLOYEE STEPS

    @When("I send a POST request to {string}")
    public void sendPostRequestToEmployees(String endpoint) {
        log.info("Thread: {}", Thread.currentThread().getId());
        String payload = FileUtils.readResourceFile(TestConfig.getEmployeePayloadPath());
        log.info("Sending POST request to {} with payload: {}", endpoint, payload);

        response = RequestBuilderFactory.createRequest(
                endpoint,
                JSON_HEADERS,
                payload
        ).post();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the response should contain the employee details")
    public void verifyResponseContainsEmployeeDetails() {
        AssertUtils.assertJsonFieldEquals(response, "name", "Alice Brown");
        AssertUtils.assertJsonFieldEquals(response, "role", "Manager");
        AssertUtils.assertJsonSchema(response, TestConfig.getEmployeeSchemaPath());
    }

    @When("I send a POST request to {string} with a payload that is missing required employee fields")
    public void sendPostRequestWithMissingFields(String endpoint) {
        String payload = FileUtils.readResourceFile("payloads/employee-missing-fields.json");
        log.info("Sending POST request to {} with missing fields payload: {}", endpoint, payload);

        response = RequestBuilderFactory.createRequest(
                endpoint,
                JSON_HEADERS,
                payload)
                .post();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the response should contain an error message stating invalid payload")
    public void verifyInvalidPayloadErrorMessage() {
        AssertUtils.assertJsonFieldEquals(response, "error", "Invalid payload");
    }

    @And("the response should include an error message stating that the employee was not found")
    public void verifyEmployeeNotFoundErrorMessage() {
        AssertUtils.assertJsonFieldEquals(response, "error", "Employee not found");
    }

    // READ EMPLOYEE STEPS

    @When("I send a GET request to the employees API")
    public void sendGetRequestToEmployeesApi() {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending GET request to /employees");

        response = RequestBuilderFactory.createRequest(TestConfig.getEmployeesEndpoint(), null, null)
                .get();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatusCode(Integer statusCode) {
        AssertUtils.assertStatusCode(response, statusCode);
    }

    @Then("the response should contain all employees as defined in the CSV file")
    public void verifyResponseContainsAllEmployeesFromCsv() {
        try {
            log.info("Verifying response contains list of employees from CSV");
            List<String[]> rows = CsvUtils.readEnvCsv(TestConfig.getEmployeesTestDataPath());
            List<String> expectedEmployees = new ArrayList<>();
            boolean firstLine = true;
            for (String[] line : rows) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (line.length < 3) continue; // skip blank or malformed lines
                expectedEmployees.add(String.format("{\"id\":%s,\"name\":\"%s\",\"role\":\"%s\"}", line[0], line[1], line[2]));
            }
            String expectedJson = "[" + String.join(",", expectedEmployees) + "]";
            log.info("Expected JSON: {}", expectedJson);
            AssertUtils.assertJsonEquals(responseBody, expectedJson, "Employee list does not match CSV");
        } catch (AssertionError e) {
            log.error("Assertion failed in verifyResponseContainsListOfEmployeesFromCSV: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in verifyResponseContainsListOfEmployeesFromCSV: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // GET EMPLOYEE BY ID

    @Given("an employee with id {int} exists")
    public void ensureEmployeeWithIdExists(int id) {
        // This is left empty as WireMock is preconfigured
    }

    @Given("an existing employee with id {int}")
    public void anExistingEmployeeWithId(int id) {
        // No implementation needed, WireMock is preconfigured
    }

    @When("I send a GET request to the employee API with id {int}")
    public void sendGetRequestToEmployeeApiWithId(int id) {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending GET request to /employees/{}", id);

        response = RequestBuilderFactory.createRequest("/employees/" + id, null, null)
                .get();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @Then("the response should contain the employee details for id {int}")
    public void verifyResponseContainsEmployeeDetailsForid(int id) {
        log.info("Verifying response contains employee details for id {}: {}", id, responseBody);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> actual = objectMapper.readValue(responseBody, Map.class);

            // Read expected employee from CSV
            List<String[]> rows = CsvUtils.readEnvCsv(TestConfig.getEmployeesTestDataPath());
            String[] expectedRow = null;
            boolean firstLine = true;
            for (String[] line : rows) {
                if (firstLine) { firstLine = false; continue; }
                if (line.length >= 3 && Integer.parseInt(line[0]) == id) {
                    expectedRow = line;
                    break;
                }
            }
            if (expectedRow == null) {
                throw new AssertionError("No employee found in CSV with id: " + id);
            }
            Map<String, Object> expected = Map.of(
                "id", Integer.parseInt(expectedRow[0]),
                "name", expectedRow[1],
                "role", expectedRow[2]
            );
            log.info("Actual employee details: {}", actual);
            log.info("Expected employee details: {}", expected);

            AssertUtils.assertJsonEquals(actual, expected, "Employee details do not match for id: " + id);
        } catch (Exception e) {
            log.error("Failed to verify employee details for id {}: {}", id, e.getMessage(), e);
            throw new AssertionError("Failed to parse/compare JSON for employee id " + id, e);
        }
    }

    @When("I send a GET request to the employee API with a non-existent id {int}")
    public void sendGetRequestToEmployeeApiWithNonExistentid(int id) {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending GET request to /employees/{} for non-existent employee", id);

        response = RequestBuilderFactory.createRequest("/employees/" + id, null, null)
                .get();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @Then("the response should contain an error message indicating the employee was not found")
    public void verifyResponseContainsEmployeeNotFoundError() {
        log.info("Verifying response contains error message for employee not found: {}", responseBody);
        AssertUtils.assertErrorMessageContains(responseBody, "Employee not found");
    }

    // UPDATE EMPLOYEE STEPS

    @And("the update payload is valid")
    public void updatePayloadIsValid() {
        // Externalized update payload to a resource file for maintainability
        responseBody = FileUtils.readResourceFile("payloads/employee-update-valid.json");
        log.info("Update payload is set to: {}", responseBody);
    }

    @When("I send a PUT request to {string} with id {int}")
    public void sendPutRequestToUpdateEmployee(String endpoint, int id) {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending PUT request to {} with id {} and payload: {}", endpoint, id, responseBody);

        response = RequestBuilderFactory.createRequest(endpoint + "/" + id, Map.of("Content-Type", "application/json"), responseBody)
                .put();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the response should contain the updated employee details")
    public void verifyResponseContainsUpdatedEmployeeDetails() {
        log.info("Verifying updated employee details: response={}", responseBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> actual = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> expected = objectMapper.readValue(FileUtils.readResourceFile("payloads/employee-update-valid.json"), Map.class);
            // Add id if present in response
            if (actual.containsKey("id")) {
                expected.put("id", actual.get("id"));
            }
            AssertUtils.assertJsonEquals(actual, expected, "Updated employee details do not match expected");
        } catch (Exception e) {
            log.error("Failed to verify updated employee details: {}", e.getMessage(), e);
            throw new AssertionError("Failed to parse/compare JSON for updated employee", e);
        }
    }

    @Given("no employee exists with id {int}")
    public void noEmployeeExistsWithId(int id) {
        // No implementation needed, WireMock is preconfigured for 404
    }

    @And("the update payload is missing required fields")
    public void updatePayloadIsMissingRequiredFields() {
        responseBody = FileUtils.readResourceFile("payloads/employee-missing-fields.json");
        log.info("Update payload is missing required fields: {}", responseBody);
    }

    // DELETE EMPLOYEE STEPS

    @When("I send a DELETE request to {string} with id {int}")
    public void sendDeleteRequestToEmployee(String endpoint, int id) {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending DELETE request to {} with id {}", endpoint, id);

        response = RequestBuilderFactory.createRequest(endpoint + "/" + id, JSON_HEADERS, null)
                .delete();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();

        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the employee should no longer exist")
    public void verifyEmployeeNoLongerExists() {
        log.info("Verifying employee no longer exists, response code: {}", responseCode);
    }

    @And("the response should contain an error message stating invalid id")
    public void verifyResponseContainsInvalidIdErrorMessage() {
        log.info("Verifying response contains error message for invalid id: {}", responseBody);
        AssertUtils.assertErrorMessageContains(responseBody, "invalid id");
    }

    @And("the response should contain an error message stating employee not found")
    public void verifyResponseContainsEmployeeNotFoundErrorMessage() {
        log.info("Verifying response contains error message for employee not found: {}", responseBody);
        AssertUtils.assertErrorMessageContains(responseBody, "Employee not found");
    }
}
