package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangePairRepository extends JpaRepository<ExchangePair, Long> {

    Optional<ExchangePair> findByExchangePairSymbol(String exchangePairSymbol);

}
