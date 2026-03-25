Feature: Trainee Management

  Scenario: Successfully register a new trainee
    When I register a trainee with firstName "Ivan" lastName "Petrov" dateOfBirth "1990-01-01" address "Moscow"
    Then the response status is 201
    And the response contains a username "Ivan.Petrov"
    And the response contains a generated password

  Scenario: Register trainee with missing first name returns 400
    When I register a trainee with firstName "" lastName "Petrov" dateOfBirth "1990-01-01" address "Moscow"
    Then the response status is 400

  Scenario: Register trainee with missing last name returns 400
    When I register a trainee with firstName "Ivan" lastName "" dateOfBirth "1990-01-01" address "Moscow"
    Then the response status is 400

  Scenario: Get trainee profile when authenticated
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I GET "/api/v1/trainees/profile"
    Then the response status is 200
    And the response contains field "username" with value "Alice.Brown"
    And the response contains field "firstName" with value "Alice"

  Scenario: Get trainee profile without authentication returns 403
    When I GET "/api/v1/trainees/profile"
    Then the response status is 403

  Scenario: Activate a trainee
    Given I am authenticated as "Alice.Brown" with role "ADMIN"
    When I PATCH "/api/v1/trainees" with param "username" "Bob.Wilson" and param "isActive" "true"
    Then the response status is 200

  Scenario: Deactivate a trainee
    Given I am authenticated as "Alice.Brown" with role "ADMIN"
    When I PATCH "/api/v1/trainees" with param "username" "Alice.Brown" and param "isActive" "false"
    Then the response status is 200

  Scenario: Activate non-existent trainee returns 404
    Given I am authenticated as "Alice.Brown" with role "ADMIN"
    When I PATCH "/api/v1/trainees" with param "username" "Ghost.User" and param "isActive" "true"
    Then the response status is 404

  Scenario: Delete trainee by admin
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I DELETE "/api/v1/trainees/Charlie.Davis"
    Then the response status is 200

  Scenario: Delete non-existent trainee returns 404
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I DELETE "/api/v1/trainees/NonExistent.User"
    Then the response status is 404

  Scenario: Update trainee trainers list
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I update trainers list with trainer usernames "John.Doe,Jane.Smith"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get available trainers for authenticated trainee
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I GET "/api/v1/trainees/available-trainers"
    Then the response status is 200
    And the response is a JSON array