Feature: Ping

  Scenario: Getting a Ping
    When I ping the endpoint
    Then I should received a 200 Http Status
