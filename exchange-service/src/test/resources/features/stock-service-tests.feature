Feature: Stock service
  Service for getting stocks from extern api, and working with them
  Scenario: Get stock by id
    When load stocks
    Then return stock with given id

  Scenario: Get all stocks
    When load stocks
    Then returning all stocks

  Scenario: Get all stocks with time series
    When load stocks
    Then returning all stocks with time series