Feature: CRM to Workload Service Integration via RabbitMQ

  Scenario: Creating a training sends ADD workload message to queue
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Integration Yoga" type "FITNESS" date "2025-07-01" duration 90
    Then the response status is 200
    And a workload ADD message was sent for trainer "John.Doe" with duration 90

  Scenario: Creating training with non-existent trainer does not send workload message
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "Ghost.Trainer" name "Yoga" type "FITNESS" date "2025-07-01" duration 60
    Then the response status is 404
    And no workload message was sent

  Scenario: Deleting a trainee sends DELETE workload messages for their trainings
    Given I am authenticated as "Charlie.Davis" with role "TRAINEE"
    And I create a training with trainee "Charlie.Davis" trainer "John.Doe" name "Test Training" type "FITNESS" date "2025-08-01" duration 60
    And I reset workload message tracking
    Given I am authenticated as "John.Doe" with role "ADMIN"
    When I DELETE "/api/v1/trainees/Charlie.Davis"
    Then the response status is 200
    And at least one workload DELETE message was sent

  Scenario: RabbitMQ failure does not prevent training creation
    Given RabbitMQ is configured to throw an exception
    And I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Resilience Test" type "FITNESS" date "2025-08-01" duration 45
    Then the response status is 200
    And the training "Resilience Test" exists in the database

  Scenario: Creating multiple trainings sends multiple ADD messages
    Given I am authenticated as "Alice.Brown" with role "TRAINEE"
    When I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Session One" type "FITNESS" date "2025-09-01" duration 60
    And I create a training with trainee "Alice.Brown" trainer "John.Doe" name "Session Two" type "FITNESS" date "2025-09-02" duration 30
    Then 2 workload ADD messages were sent for trainer "John.Doe"