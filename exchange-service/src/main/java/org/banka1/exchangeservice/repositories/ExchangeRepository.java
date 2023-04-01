package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Exchange findByExcId(Long id);

    List<Exchange> findByExcNameLike(String excName);

    Exchange findByExcMicCode(String excMicCode);

    Exchange findByExcAcronym(String excAcronym);
}
