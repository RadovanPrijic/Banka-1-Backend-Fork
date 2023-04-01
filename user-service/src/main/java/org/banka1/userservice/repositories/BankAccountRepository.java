package org.banka1.userservice.repositories;

import org.banka1.userservice.domains.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    BankAccount findByUser_Id(Long userId);
    BankAccount findByUser_Email(String email);

}
