Feature: Update Employee
  As an API client
  I want to update employee details
  So that the employee information is kept current

  @positive
  Scenario: Successfully update an employee
    Given an existing employee with id 1
    And the update payload is valid
    When I send a PUT request to "/employees" with id 1
    Then the response status should be 200
    And the response should contain the updated employee details

  @negative
  Scenario: Fail to update employee with invalid id
    Given no employee exists with id 999
    And the update payload is valid
    When I send a PUT request to "/employees" with id 999
    Then the response status should be 404
    And the response should include an error message stating that the employee was not found

  @negative
  Scenario: Fail to update employee with invalid payload
    Given an existing employee with id 1
    And the update payload is missing required fields
    When I send a PUT request to "/employees" with id 1
    Then the response status should be 400
    And the response should contain an error message stating invalid payload

