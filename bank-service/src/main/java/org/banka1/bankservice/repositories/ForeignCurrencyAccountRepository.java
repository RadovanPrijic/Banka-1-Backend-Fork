package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.Currency;
import org.banka1.bankservice.domains.entities.ForeignCurrencyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForeignCurrencyAccountRepository extends JpaRepository<ForeignCurrencyAccount, Long> {
}
