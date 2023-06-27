package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.repositories.BusinessAccountRepository;
import org.banka1.bankservice.repositories.CurrentAccountRepository;
import org.banka1.bankservice.repositories.ForeignCurrencyAccountRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    private final CurrentAccountRepository currentAccountRepository;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final BusinessAccountRepository businessAccountRepository;

    public AccountService(CurrentAccountRepository currentAccountRepository, ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                            BusinessAccountRepository businessAccountRepository) {
        this.currentAccountRepository = currentAccountRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
    }
}
