package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllBySenderId(Long id);
    List<Payment> findAllBySenderAccountNumber(String accountNumber);

}
