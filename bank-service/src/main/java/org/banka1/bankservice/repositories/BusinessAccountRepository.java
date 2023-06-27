package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.account.BusinessAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessAccountRepository extends JpaRepository<BusinessAccount, Long> {
}
