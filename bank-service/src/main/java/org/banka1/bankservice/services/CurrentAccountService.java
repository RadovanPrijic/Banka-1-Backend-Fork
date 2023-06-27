package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.repositories.CurrentAccountRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CurrentAccountService {
    private final CurrentAccountRepository accountRepository;

    public CurrentAccountService(CurrentAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
