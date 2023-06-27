package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.repositories.BusinessAccountRepository;
import org.banka1.bankservice.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
}
