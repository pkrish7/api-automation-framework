package stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import utils.AssertUtils;
import utils.CsvUtils;
import utils.RequestBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmployeeStepDefs {
    private int responseCode;
    private String responseBody;
    private Response response;

    // CREATE EMPLOYEE STEPS
    @When("I send a POST request to {string}")
    public void sendPostRequestToEmployees(String endpoint) {
        log.info("Thread: " + Thread.currentThread().getId());
        String payload;
        try {
            payload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/employee.json")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read employee payload from file", e);
        }
        log.info("Sending POST request to {} with payload: {}", endpoint, payload);
        response = RequestBuilderFactory.createRequest(endpoint, Map.of("Content-Type", "application/json"), payload)
                .post();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the response should contain the employee details")
    public void verifyResponseContainsEmployeeDetails() {
        AssertUtils.assertJsonFieldEquals(response, "name", "Alice Brown");
        AssertUtils.assertJsonFieldEquals(response, "role", "Manager");
        // Validate response against employee schema
        AssertUtils.assertJsonSchema(response, "schemas/employee-schema.json");
    }

    @When("I send a POST request to {string} with a payload that is missing required employee fields")
    public void sendPostRequestWithMissingFields(String endpoint) {
        String payload = "{\"name\":\"\"}";
        log.info("Sending POST request to {} with missing fields payload: {}", endpoint, payload);
        response = RequestBuilderFactory.createRequest(endpoint, Map.of("Content-Type", "application/json"), payload)
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
        response = RequestBuilderFactory.createRequest("/employees", null, null)
                .get();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatusCode(Integer statusCode) {
        AssertUtils.assertStatusCode(response, statusCode);
    }

    @Then("the response should contain a list of employees")
    public void verifyResponseContainsListOfEmployees() {
        log.info("Verifying response contains list of employees");
        List<String[]> rows = CsvUtils.readEnvCsv("employees.csv");
        List<String> expectedEmployees = new ArrayList<>();
        boolean firstLine = true;
        for (String[] line : rows) {
            if (firstLine) { firstLine = false; continue; } // skip header
            if (line.length < 3) continue; // skip blank or malformed lines
            expectedEmployees.add(String.format("{\"id\":%s,\"name\":\"%s\",\"role\":\"%s\"}", line[0], line[1], line[2]));
        }
        String expectedJson = "[" + String.join(",", expectedEmployees) + "]";
        Assert.assertEquals(responseBody, expectedJson);
    }

    @Then("the response should contain a list of employees from CSV")
    public void verifyResponseContainsListOfEmployeesFromCSV() {
        try {
            log.info("Verifying response contains list of employees from CSV");
            List<String[]> rows = CsvUtils.readEnvCsv("employees.csv");
            List<String> expectedEmployees = new ArrayList<>();
            boolean firstLine = true;
            for (String[] line : rows) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (line.length < 3) continue; // skip blank or malformed lines
                expectedEmployees.add(String.format("{\"id\":%s,\"name\":\"%s\",\"role\":\"%s\"}", line[0], line[1], line[2]));
            }
            String expectedJson = "[" + String.join(",", expectedEmployees) + "]";
            Assert.assertEquals(responseBody, expectedJson);
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
    public void ensureEmployeeWithidExists(int id) {
        // This is left empty as WireMock is preconfigured
    }

    @Given("an existing employee with id {int}")
    public void anExistingEmployeeWithId(int id) {
        // No implementation needed, WireMock is preconfigured
    }

    @When("I send a GET request to the employee API with id {int}")
    public void sendGetRequestToEmployeeApiWithid(int id) {
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
            Map<String, Object> expected = objectMapper.readValue("{\"id\":1,\"name\":\"John Doe\",\"role\":\"Developer\"}", Map.class);
            Assert.assertEquals(actual, expected);
        } catch (Exception e) {
            throw new AssertionError("Failed to parse JSON for comparison", e);
        }
    }

    @When("I send a GET request to the employee API with a non-existent id {int}")
    public void sendGetRequestToEmployeeApiWithNonExistentid(int id) {
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
        Assert.assertEquals(responseBody, ("{\"error\":\"Employee not found\"}"));
    }

    // UPDATE EMPLOYEE STEPS
    @And("the update payload is valid")
    public void updatePayloadIsValid() {
        responseBody = "{ \"name\": \"John Doe Updated\", \"role\": \"Lead\" }";
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
        String name = response.jsonPath().getString("name");
        String role = response.jsonPath().getString("role");
        log.info("Verifying updated employee details: name={}, role={}", name, role);
        Assert.assertEquals(name, "John Doe Updated");
        Assert.assertEquals(role, "Lead");
    }

    @Given("no employee exists with id {int}")
    public void noEmployeeExistsWithId(int id) {
        // No implementation needed, WireMock is preconfigured for 404
    }

    @And("the update payload is missing required fields")
    public void updatePayloadIsMissingRequiredFields() {
        responseBody = "{ \"name\": \"\" }";
        log.info("Update payload is missing required fields: {}", responseBody);
    }

    // DELETE EMPLOYEE STEPS
    @When("I send a DELETE request to {string} with id {int}")
    public void sendDeleteRequestToEmployee(String endpoint, int id) {
        log.info("Thread: " + Thread.currentThread().getId());
        log.info("Sending DELETE request to {} with id {}", endpoint, id);
        response = RequestBuilderFactory.createRequest(endpoint + "/" + id, Map.of("Content-Type", "application/json"), null)
                .delete();
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
        log.info("Received response: status={}, body={}", responseCode, responseBody);
    }

    @And("the employee should no longer exist")
    public void verifyEmployeeNoLongerExists() {
        log.info("Verifying employee no longer exists, response code: {}", responseCode);
        Assert.assertEquals(responseCode, 204);
    }

    @And("the response should contain an error message stating invalid id")
    public void verifyResponseContainsInvalidIdErrorMessage() {
        log.info("Verifying response contains error message for invalid id: {}", responseBody);
        Assert.assertTrue(responseBody.contains("invalid id") || responseBody.contains("Employee not found"));
    }
}
