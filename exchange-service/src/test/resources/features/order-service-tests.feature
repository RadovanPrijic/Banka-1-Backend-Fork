Feature: Order service
  Service for working with all order-related things

  Scenario: Making order
    When User logged in
    And Order is made
    Then Get order

  Scenario: Getting user listings
    When User logged in
    And User listing is made
    Then Get user listing

  Scenario: Getting user from user service
    When User logged in
    Then Get user information from user service

  Scenario: Approving order successfully
    When User logged in
    And Order is made
    Then Approve and get order

  Scenario: Rejecting order successfully
    When User logged in
    And Order is made
    Then Reject and get order

  Scenario: Approving order unsuccessfully
    When User logged in
    Then An invalid rejected order search is made

  Scenario: Rejecting order unsuccessfully
    When User logged in
    Then An invalid approved order search is made

  Scenario: Reducing user's daily limit
    When User logged in
    Then User daily limit can be reduced

  Scenario: Updating user's bank account balance
    When User logged in
    Then User bank account balance can be updated

