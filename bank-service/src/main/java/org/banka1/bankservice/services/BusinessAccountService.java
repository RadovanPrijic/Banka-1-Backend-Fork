package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.repositories.BusinessAccountRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessAccountService {
    private final BusinessAccountRepository businessAccountRepository;

    public BusinessAccountService(BusinessAccountRepository businessAccountRepository) {
        this.businessAccountRepository = businessAccountRepository;
    }
}
