package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.OptionBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionBetRepository extends JpaRepository<OptionBet, Long> {
    List<OptionBet> findAllByUserId(Long userId);
}
