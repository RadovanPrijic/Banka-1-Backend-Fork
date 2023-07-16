Feature: Servis za uplatama

  Scenario: Marko radi transfer izmedju dva racuna
    Given Zaposleni se uloguje i otvara Marku dva tekuca racuna
    When Marko se uloguje i radi transfer iz prvog tekuceg racuna u drugi tekuci racun
    Then Stanje se promenilo na prvom i drugom racunu

    Scenario: Marko radi transfer izmedju dva devizna racuna
      Given Zaposleni se uloguje i otvara Marku devizni racun jedan za EUR drugi za EUR
      When Marko se uloguje i radi transfer iz prvog deviznog racuna u drugi devizni racun
      Then Stanje se promenilo na deviznim racunima

  Scenario: Kompanija Mirko.doo uplacuje drugoj kompaniji Rajko.doo
    Given Zaposleni pravi dve kompanije Mirko.doo i Rajko.doo respektivno
    When Marko,Vlasnik kompanije Mirko.doo, se uloguje i uplacuje iznos novca kompaniji Rajko.doo
    Then Mirko.doo ima manji iznos novca, dok Rajko.doo ima veci iznos novca