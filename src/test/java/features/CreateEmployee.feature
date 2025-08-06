@regression
Feature: Create Employee
  As an API client
  I want to create a new employee
  So that the employee is added to the system

    @smoke @positive
  Scenario: Successfully create an employee
    When I send a POST request to "/employees"
    Then the response status should be 201
    And the response should contain the employee details

    @negative
  Scenario: Fail to create employee with missing fields
    When I send a POST request to "/employees" with a payload that is missing required employee fields
    Then the response status should be 400
    And the response should contain an error message stating invalid payload
