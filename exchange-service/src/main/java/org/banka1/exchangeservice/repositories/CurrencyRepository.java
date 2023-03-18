package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
