package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.ForeignCurrencyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForeignCurrencyAccountRepository extends JpaRepository<ForeignCurrencyAccount, Long> {

    List<ForeignCurrencyAccount> findAllByOwnerId(Long id);

}
