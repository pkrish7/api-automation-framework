package steps;

import config.TestConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class EmployeeSteps {
    private int responseCode;
    private String responseBody;
    private Response response;

    @BeforeClass
    public void startWireMockServer() {
        TestConfig.startWireMock();
    }

    @AfterClass
    public void stopWireMockServer() {
        TestConfig.stopWireMock();
    }

    // CREATE EMPLOYEE STEPS
    @When("I send a POST request to {string}")
    public void sendPostRequestToEmployees(String endpoint) {
        String payload = "{ \"name\": \"Alice Brown\", \"role\": \"Manager\" }";
        response = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .post(TestConfig.BASE_URL + endpoint);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @And("the response should contain the employee details")
    public void verifyResponseContainsEmployeeDetails() {
        String name = response.jsonPath().getString("name");
        String role = response.jsonPath().getString("role");
        Assert.assertEquals(name, "Alice Brown");
        Assert.assertEquals(role, "Manager");
    }

    // SCENARIO: Fail to create employee with missing fields
    @When("I send a POST request to {string} with a payload that is missing required employee fields")
    public void sendPostRequestWithMissingFields(String endpoint) {
        String payload = "{\"name\":\"\"}";
        Response response = given()
                .contentType("application/json")
                .body(payload)
                .post(TestConfig.BASE_URL + endpoint);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @And("the response should contain an error message stating invalid payload")
    public void verifyInvalidPayloadErrorMessage() {
        Assert.assertEquals(responseBody, "{\"error\":\"Invalid payload\"}");
    }

    @And("the response should include an error message stating that the employee was not found")
    public void verifyEmployeeNotFoundErrorMessage() {
        Assert.assertEquals(responseBody, "{\"error\":\"Employee not found\"}");
    }

    // READ EMPLOYEE STEPS
    @When("I send a GET request to the employees API")
    public void sendGetRequestToEmployeesApi() {
        response = given()
                .get(TestConfig.BASE_URL + "/employees");
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatusCode(Integer statusCode) {
        Assert.assertEquals(responseCode, statusCode.intValue());
    }

    @Then("the response should contain a list of employees")
    public void verifyResponseContainsListOfEmployees() {
        Assert.assertEquals(responseBody, "[{\"id\":1,\"name\":\"John Doe\",\"role\":\"Developer\"},{\"id\":2,\"name\":\"Jane Smith\",\"role\":\"Tester\"}]");
    }

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
        response = given()
                .get(TestConfig.BASE_URL + "/employees/" + id);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @Then("the response should contain the employee details for id {int}")
    public void verifyResponseContainsEmployeeDetailsForid(int id) {
        Assert.assertEquals(responseBody, "{\"id\":1,\"name\":\"John Doe\",\"role\":\"Developer\"}");
    }

    @When("I send a GET request to the employee API with a non-existent id {int}")
    public void sendGetRequestToEmployeeApiWithNonExistentid(int id) {
        response = given()
                .get(TestConfig.BASE_URL + "/employees/" + id);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @Then("the response should contain an error message indicating the employee was not found")
    public void verifyResponseContainsEmployeeNotFoundError() {
        Assert.assertEquals(responseBody, ("{\"error\":\"Employee not found\"}"));
    }

    @And("the update payload is valid")
    public void updatePayloadIsValid() {
        responseBody = "{ \"name\": \"John Doe Updated\", \"role\": \"Lead\" }";
    }

    @When("I send a PUT request to {string} with id {int}")
    public void sendPutRequestToUpdateEmployee(String endpoint, int id) {
        response = given()
                .header("Content-Type", "application/json")
                .body(responseBody)
                .put(TestConfig.BASE_URL + endpoint + "/" + id);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @And("the response should contain the updated employee details")
    public void verifyResponseContainsUpdatedEmployeeDetails() {
        String name = response.jsonPath().getString("name");
        String role = response.jsonPath().getString("role");
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
    }

    // DELETE EMPLOYEE STEPS
    @When("I send a DELETE request to {string} with id {int}")
    public void sendDeleteRequestToEmployee(String endpoint, int id) {
        response = given()
                .header("Content-Type", "application/json")
                .delete(TestConfig.BASE_URL + endpoint + "/" + id);
        responseCode = response.getStatusCode();
        responseBody = response.getBody().asString();
    }

    @And("the employee should no longer exist")
    public void verifyEmployeeNoLongerExists() {
        Assert.assertEquals(responseCode, 204);
    }

    @And("the response should contain an error message stating invalid id")
    public void verifyResponseContainsInvalidIdErrorMessage() {
        Assert.assertTrue(responseBody.contains("invalid id") || responseBody.contains("Employee not found"));
    }
}
