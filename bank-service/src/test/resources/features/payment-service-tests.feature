Feature: Servis za uplatama

  Scenario: Harold radi transfer izmedju dva racuna
    Given Harold je otvorio dva tekuca racuna i Harold se ulogovao
    When Harold radi transfer iz prvog u drugi racun
    Then Stanje se promenilo na prvom i drugom racunu

    Scenario: Harold radi transfer izmedju dva devizna racuna
      Given Harold pravi devizni racun jedan za EUR drugi za EUR
      When Harold radi transfer iz prvog deviznog racuna u drugi devizni racun
      Then Stanje se promenilo na deviznim racunima

  Scenario: Kompanija uplacuje drugoj kompaniji
    Given Postoje dve kompanije, Mirko.doo i Rajko.doo respektivno
    When Mirko.doo uplacuje iznos novca Rajko.doo
    Then Mirko.doo ima manji iznos novca, dok Rajko.doo ima veci iznos novca