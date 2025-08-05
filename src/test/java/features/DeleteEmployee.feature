Feature: Delete Employee
  As an API client
  I want to delete an employee
  So that the employee is removed from the system

    @positive
  Scenario: Successfully delete an employee
    Given an existing employee with id 1
    When I send a DELETE request to "/employees" with id 1
    Then the response status should be 204
    And the employee should no longer exist

    @negative
  Scenario: Fail to delete employee with invalid id
    Given no employee exists with id 999
    When I send a DELETE request to "/employees" with id 999
    Then the response status should be 404
    And the response should contain an error message stating invalid id

