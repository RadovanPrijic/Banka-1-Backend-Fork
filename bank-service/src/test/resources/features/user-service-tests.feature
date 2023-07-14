Feature: Servis za upravljanje korisnicima

  Scenario: Azuriranje klijenta preko servisa
    Given Postoji zaposleni
    When Kreira klijenta preko servisa
    And Azurira klijenta preko servisa
    Then Trazi klijenta preko servisa

    Scenario: Logovanje korisnika
      Given Postoji zaposleni
      Then Zaposleni se ulogovao

  Scenario: Korisnik unosi pogresne kredencijale
    Given Postoji zaposleni
    When Korisnik unosi pogresne kredencijale
    Then Zaposleni se ulogovao

  Scenario: Kreiranje korisnika
    Given Postoji zaposleni
    And Zaposleni se uloguje
    When Kreira korisnika
    Then Pronadje korisnika

  Scenario: Azuriranje korisnika i korisnik se uloguje
    Given Postoji zaposleni
    And Zaposleni se uloguje
    When Azurira korisnika
    Then Azuriran korisnik se uloguje

    Scenario: Izvlacenje svih klijenta
      Given Postoji zaposleni
      And Zaposleni se uloguje
      Then Zaposleni izvlaci sve klijente

  Scenario: Izvlacenje svih klijenta filtrirano
    Given Postoji zaposleni
    And Zaposleni se uloguje
    Then Zaposleni izvlaci sve klijente filtrirano

  Scenario: Izvlacenje informacija o meni tj. logovanom korisniku
    Given Postoji zaposleni
    And Zaposleni se uloguje
    Then Izvlacim informacije o meni


    Scenario: Resetovanje sifre
      Given Postoji zaposleni
      When Zaboravio sifru
      Then Resetuje sifru
      And Zaposleni se ponovo uloguje