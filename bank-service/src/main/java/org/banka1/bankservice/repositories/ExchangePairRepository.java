package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangePairRepository extends JpaRepository<ExchangePair, Long> {
}
