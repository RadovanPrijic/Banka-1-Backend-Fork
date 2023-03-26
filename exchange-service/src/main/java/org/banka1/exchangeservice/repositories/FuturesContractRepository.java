package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.FuturesContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuturesContractRepository extends JpaRepository<FuturesContract, Long> {

}