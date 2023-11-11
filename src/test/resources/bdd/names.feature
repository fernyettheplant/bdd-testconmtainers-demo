Feature: Name

  Scenario: Post a new name
    Given a name "fernando"
    When I submit a new name
    Then Should be saved in database
