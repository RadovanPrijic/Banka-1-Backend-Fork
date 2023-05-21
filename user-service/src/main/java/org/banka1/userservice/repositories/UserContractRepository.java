package org.banka1.userservice.repositories;

import org.banka1.userservice.domains.entities.UserContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContractRepository extends JpaRepository<UserContract, Long> {

    UserContract findAllByContractId(String contractId);

}
