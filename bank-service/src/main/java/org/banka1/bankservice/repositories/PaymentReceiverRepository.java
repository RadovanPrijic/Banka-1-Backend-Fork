package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentReceiverRepository extends JpaRepository<PaymentReceiver, Long> {
}
