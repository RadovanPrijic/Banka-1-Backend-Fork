# Created by DrRale at 03-Apr-23
Feature: Servis za upravljanje berzama

  Ovaj servis sluzi da bi berza perzistirala u bazu podataka.

  Scenario: Dodavanje berze
    When Stavlja se lista berze
    Then Berza je sacuvana u bazi podataka