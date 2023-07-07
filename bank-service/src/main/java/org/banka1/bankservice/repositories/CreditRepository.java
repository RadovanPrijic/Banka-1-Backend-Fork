package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.credit.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findAllByClientIdOrderByCreditAmountDesc(Long clientId);
    List<Credit> findAllByAccountNumberOrderByCreditAmountDesc(String accountNumber);

}
