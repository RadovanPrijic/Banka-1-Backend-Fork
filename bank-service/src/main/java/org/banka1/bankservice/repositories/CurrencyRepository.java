package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCurrencyName(String name);
    Optional<Currency> findByCurrencySymbol(String symbol);

}
