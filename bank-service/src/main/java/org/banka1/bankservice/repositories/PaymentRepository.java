package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllBySenderId(Long id);
    List<Payment> findAllBySenderAccountNumber(String accountNumber);

}
