package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.credit.InterestRatePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRatePaymentRepository extends JpaRepository<InterestRatePayment, Long> {

    List<InterestRatePayment> findAllByCreditId(Long creditId);

}
