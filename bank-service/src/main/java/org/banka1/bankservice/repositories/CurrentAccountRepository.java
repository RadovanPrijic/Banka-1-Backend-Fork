package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.Currency;
import org.banka1.bankservice.domains.entities.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
}
