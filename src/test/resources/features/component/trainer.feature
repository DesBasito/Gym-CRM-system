Feature: Trainer Management

  Scenario: Successfully register a new trainer
    When I register a trainer with firstName "Sergey" lastName "Ivanov" specialization "FITNESS"
    Then the response status is 201
    And the response contains a username "Sergey.Ivanov"
    And the response contains a generated password

  Scenario: Register trainer with missing first name returns 400
    When I register a trainer with firstName "" lastName "Ivanov" specialization "FITNESS"
    Then the response status is 400

  Scenario: Register trainer with missing last name returns 400
    When I register a trainer with firstName "Sergey" lastName "" specialization "FITNESS"
    Then the response status is 400

  Scenario: Register trainer with missing specialization returns 400
    When I register a trainer with firstName "Sergey" lastName "Ivanov" specialization ""
    Then the response status is 400

  Scenario: Register trainer with invalid specialization returns 400
    When I register a trainer with firstName "Sergey" lastName "Ivanov" specialization "DANCING"
    Then the response status is 400

  Scenario: Get trainer profile when authenticated
    Given I am authenticated as "John.Doe" with role "TRAINER"
    When I GET "/api/v1/trainers/profile"
    Then the response status is 200
    And the response contains field "username" with value "John.Doe"
    And the response contains field "firstName" with value "John"

  Scenario: Get trainer profile without authentication returns 403
    When I GET "/api/v1/trainers/profile"
    Then the response status is 403

  Scenario: Activate a trainer
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I PATCH "/api/v1/trainers" with param "username" "Jane.Smith" and param "isActive" "true"
    Then the response status is 200

  Scenario: Deactivate a trainer
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I PATCH "/api/v1/trainers" with param "username" "John.Doe" and param "isActive" "false"
    Then the response status is 200

  Scenario: Activate non-existent trainer returns 404
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I PATCH "/api/v1/trainers" with param "username" "Ghost.Trainer" and param "isActive" "true"
    Then the response status is 404