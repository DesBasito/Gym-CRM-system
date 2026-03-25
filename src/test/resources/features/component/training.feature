Feature: Training Management

  Scenario: Get all training types
    When I GET "/api/v1/trainings/types"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get all training types is publicly accessible
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I GET "/api/v1/trainings/types"
    Then the response status is 200

  Scenario: Successfully create a training session
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Morning Yoga" type "FITNESS" date "2025-06-01" duration 60
    Then the response status is 200

  Scenario: Create training with non-existent trainee returns 404
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Ghost.Trainee" trainer "John.Doe" name "Yoga" type "FITNESS" date "2025-06-01" duration 60
    Then the response status is 404

  Scenario: Create training with non-existent trainer returns 404
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "Ghost.Trainer" name "Yoga" type "FITNESS" date "2025-06-01" duration 60
    Then the response status is 404

  Scenario: Create training with missing training name returns 400
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "" type "FITNESS" date "2025-06-01" duration 60
    Then the response status is 400

  Scenario: Create training with invalid training type returns 400
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Session" type "DANCING" date "2025-06-01" duration 60
    Then the response status is 400

  Scenario: Get trainee trainings with authentication
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I GET trainee trainings for username "Alice.Brown"
    Then the response status is 200
    And the response is a JSON array

  Scenario: Get trainee trainings without authentication returns 403
    When I GET trainee trainings for username "Alice.Brown"
    Then the response status is 403

  Scenario: Get trainer trainings with authentication
    Given I am authenticated as "John.Doe" with role "TRAINER"
    When I GET trainer trainings for username "John.Doe"
    Then the response status is 200
    And the response is a JSON array