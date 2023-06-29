package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
