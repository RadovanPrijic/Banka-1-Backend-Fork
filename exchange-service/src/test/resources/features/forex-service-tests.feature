Feature: Forex service
  Service for getting forexes from extern api, and working with them
  Scenario: Get forexes test
    When load forexes
    Then returning forexes

  Scenario: Get forexes with time series
    When load forexes
    Then returning forexes with time series
