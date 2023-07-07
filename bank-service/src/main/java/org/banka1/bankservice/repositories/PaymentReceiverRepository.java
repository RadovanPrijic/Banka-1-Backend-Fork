package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentReceiverRepository extends JpaRepository<PaymentReceiver, Long> {

    List<PaymentReceiver> findAllBySenderId(Long id);

}
