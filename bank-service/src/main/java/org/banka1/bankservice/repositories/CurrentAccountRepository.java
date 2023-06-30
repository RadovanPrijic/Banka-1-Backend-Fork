package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.CurrentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {

    List<CurrentAccount> findAllByOwnerId(Long id);
    Optional<CurrentAccount> findByAccountNumber(String accountNumber);

}
