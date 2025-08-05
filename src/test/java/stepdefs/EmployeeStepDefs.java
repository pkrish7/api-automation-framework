package stepdefs;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

public class EmployeeStepDefs {
    private Response response;
    private String payload;

    // CREATE
    @Given("the employee payload is valid")
    public void valid_employee_payload() {
        payload = "{\"name\":\"John Doe\",\"role\":\"Developer\"}";
    }

    @Given("the employee payload is missing required fields")
    public void invalid_employee_payload() {
        payload = "{\"role\":\"Developer\"}";
    }

    @When("I send a POST request to \"/employees\"")
    public void send_post_request() {
        response = RestAssured.given().contentType("application/json").body(payload).post("/employees");
    }

    @Then("the response status should be 201")
    public void response_status_201() {
        Assert.assertEquals(response.getStatusCode(), 201);
    }

    @Then("the response should contain the employee details")
    public void response_contains_employee_details() {
        Assert.assertTrue(response.getBody().asString().contains("name"));
    }

    @Then("the response status should be 400")
    public void response_status_400() {
        Assert.assertEquals(response.getStatusCode(), 400);
    }

    @Then("the response should contain an error message")
    public void response_contains_error_message() {
        Assert.assertTrue(response.getBody().asString().toLowerCase().contains("error"));
    }

    // READ
    @When("I send a GET request to \"/employees\"")
    public void send_get_request() {
        response = RestAssured.get("/employees");
    }

    @When("I send a GET request to \"/employees\" with no employees present")
    public void send_get_request_no_employees() {
        response = RestAssured.get("/employees");
    }

    @Then("the response status should be 200")
    public void response_status_200() {
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Then("the response should contain a list of employees")
    public void response_contains_list_of_employees() {
        Assert.assertTrue(response.getBody().asString().startsWith("["));
    }

    @Then("the response should be an empty list")
    public void response_empty_list() {
        Assert.assertEquals("[]", response.getBody().asString());
    }

    @When("I send a GET request to \"/employeez\"")
    public void send_get_invalid_endpoint() {
        response = RestAssured.get("/employeez");
    }

    @Then("the response status should be 404")
    public void response_status_404() {
        Assert.assertEquals(404, response.getStatusCode());
    }

    // UPDATE
    @Given("an existing employee with id {int}")
    public void existing_employee_with_id(int id) {
        // Assume employee exists in mock server
    }

    @Given("the update payload is valid")
    public void valid_update_payload() {
        payload = "{\"name\":\"John Doe Updated\",\"role\":\"Lead\"}";
    }

    @Given("the update payload is missing required fields")
    public void invalid_update_payload() {
        payload = "{\"role\":\"Lead\"}";
    }

    @When("I send a PUT request to \"/employees/{int}\"")
    public void send_put_request(int id) {
        response = RestAssured.given().contentType("application/json").body(payload).put("/employees/" + id);
    }

    @Then("the response should contain the updated employee details")
    public void response_contains_updated_employee_details() {
        Assert.assertTrue(response.getBody().asString().contains("Updated"));
    }

    // DELETE
    @When("I send a DELETE request to \"/employees/{int}\"")
    public void send_delete_request(int id) {
        response = RestAssured.delete("/employees/" + id);
    }

    @Then("the response status should be 204")
    public void response_status_204() {
        Assert.assertEquals(204, response.getStatusCode());
    }

    @Then("the employee should no longer exist")
    public void employee_should_no_longer_exist() {
        Response check = RestAssured.get("/employees/1");
        Assert.assertTrue(check.getBody().asString().equals("[]") || check.getStatusCode() == 404);
    }
}

