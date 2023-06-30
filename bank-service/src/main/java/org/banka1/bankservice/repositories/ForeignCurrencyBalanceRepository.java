package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.ForeignCurrencyBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForeignCurrencyBalanceRepository extends JpaRepository<ForeignCurrencyBalance, Long> {
}
