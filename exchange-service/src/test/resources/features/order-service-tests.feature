Feature: Order service
  Service for working with orders
  Scenario: Making order
    When Make order
    Then Get order

  Scenario: Getting user listings
    When User listing is made
    Then Get user listing
