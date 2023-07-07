package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.ForeignCurrencyBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForeignCurrencyBalanceRepository extends JpaRepository<ForeignCurrencyBalance, Long> {
}
