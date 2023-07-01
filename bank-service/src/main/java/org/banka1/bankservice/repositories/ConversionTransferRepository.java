package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversionTransferRepository extends JpaRepository<ConversionTransfer, Long> {

    List<ConversionTransfer> findAllBySenderId(Long id);
    List<ConversionTransfer> findAllBySenderAccountNumber(String accountNumber);

}
