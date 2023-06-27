package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.BusinessAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessAccountRepository extends JpaRepository<BusinessAccount, Long> {

    List<BusinessAccount> findAllByOwnerId(Long id);

}
