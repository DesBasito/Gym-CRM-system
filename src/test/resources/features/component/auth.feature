Feature: Authentication

  Scenario: Successful login with valid trainee credentials
    When I login with username "Alice.Brown" and password "qwe"
    Then the response status is 200
    And the response contains a JWT token

  Scenario: Successful login with valid trainer credentials
    When I login with username "John.Doe" and password "qwe"
    Then the response status is 200
    And the response contains a JWT token

  Scenario: Login fails with wrong password
    When I login with username "Alice.Brown" and password "wrongPassword"
    Then the response status is 401

  Scenario: Login fails for non-existent user
    When I login with username "Ghost.User" and password "qwe"
    Then the response status is 401

  Scenario: Login fails with empty username
    When I login with username "" and password "qwe"
    Then the response status is 400