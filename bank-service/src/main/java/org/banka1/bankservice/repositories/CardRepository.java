package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOwnerId(Long id);
    List<Card> findAllByAccountNumber(String accountNumber);

}
