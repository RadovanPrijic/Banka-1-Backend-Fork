package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.credit.CreditRequest;
import org.banka1.bankservice.domains.entities.credit.CreditRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    List<CreditRequest> findAllByCreditRequestStatus(CreditRequestStatus creditRequestStatus);
    List<CreditRequest> findAllByClientEmail(String clientEmail);
    List<CreditRequest> findAllByClientEmailAndCreditRequestStatus(String clientEmail, CreditRequestStatus creditRequestStatus);
    List<CreditRequest> findAllByAccountNumber(String accountNumber);

}
