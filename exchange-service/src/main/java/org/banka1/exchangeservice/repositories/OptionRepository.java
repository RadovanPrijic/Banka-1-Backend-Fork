package org.banka1.exchangeservice.repositories;

import org.banka1.exchangeservice.domains.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OptionRepository extends JpaRepository<Option, Long>, QuerydslPredicateExecutor<Option> {

}
