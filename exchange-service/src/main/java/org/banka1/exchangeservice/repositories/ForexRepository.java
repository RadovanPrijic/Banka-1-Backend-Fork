package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Forex;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForexRepository extends JpaRepository<Forex, Long>, QuerydslPredicateExecutor<Forex> {

}
