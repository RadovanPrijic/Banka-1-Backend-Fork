package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Forex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForexRepository extends JpaRepository<Forex, Long> {

    Optional<Forex> findBySymbol(String symbol);
}
