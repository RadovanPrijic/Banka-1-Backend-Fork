package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.user.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BankUser, Long>, QuerydslPredicateExecutor<BankUser> {

    Optional<BankUser> findByEmail(String email);

}
