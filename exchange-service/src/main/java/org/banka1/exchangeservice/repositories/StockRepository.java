package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

}
