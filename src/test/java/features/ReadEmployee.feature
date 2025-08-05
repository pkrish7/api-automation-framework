Feature: Read Employee
  As an API client
  I want to retrieve employee details
  So that I can view employee information

  @positive
  Scenario: Successfully retrieve all employees
    When I send a GET request to the employees API
    Then the response status should be 200
    And the response should contain a list of employees

  @positive
  Scenario: Successfully retrieve a specific employee by id
    Given an employee with id 1 exists
    When I send a GET request to the employee API with id 1
    Then the response status should be 200
    And the response should contain the employee details for id 1

  @negative
  Scenario: Retrieve employee with non-existent id
    When I send a GET request to the employee API with a non-existent id 999
    Then the response status should be 404
    And the response should contain an error message indicating the employee was not found