package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.credit.CreditInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditInstallmentRepository extends JpaRepository<CreditInstallment, Long> {

    List<CreditInstallment> findAllByCreditId(Long creditId);

}
