Feature: Servis za upravljanje sa valutama

  Ovaj servis sluzi da bi se valute perzistirale u bazu podataka.

  Scenario: Dodavanje valuta
    When dodaju se nove valute
    Then valute se sacuvaju u bazi podataka


