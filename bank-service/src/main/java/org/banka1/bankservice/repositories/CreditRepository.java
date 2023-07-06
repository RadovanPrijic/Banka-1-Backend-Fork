package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.credit.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {



}
