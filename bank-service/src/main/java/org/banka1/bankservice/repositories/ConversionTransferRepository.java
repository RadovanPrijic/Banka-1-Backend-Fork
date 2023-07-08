package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversionTransferRepository extends JpaRepository<ConversionTransfer, Long> {

    List<ConversionTransfer> findAllBySenderId(Long id);
    List<ConversionTransfer> findAllBySenderAccountNumber(String accountNumber);

}
