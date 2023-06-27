package org.banka1.bankservice.repositories;

import org.banka1.bankservice.domains.entities.Company;
import org.banka1.bankservice.domains.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
