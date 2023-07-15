Feature: Servis za uplatama

  Scenario: Harold radi transfer izmedju dva racuna
    Given Harold je otvorio dva tekuca racuna i Harold se ulogovao
    When Harold radi transfer iz prvog u drugi racun
    Then Stanje se promenilo na prvom i drugom racunu