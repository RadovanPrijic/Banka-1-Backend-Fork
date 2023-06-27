package org.banka1.bankservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessAccountRepository extends JpaRepository<org.banka1.bankservice.domains.entities.BusinessAccount, Long> {
}
