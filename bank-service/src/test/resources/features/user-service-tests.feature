Feature: Servis za upravljanje korisnicima
  Scenario: Azuriranje korisnika
    When Zaposleni se uloguje
    And Kreira klijenta
    And Azurira korisnika
    Then Trazi korisnika