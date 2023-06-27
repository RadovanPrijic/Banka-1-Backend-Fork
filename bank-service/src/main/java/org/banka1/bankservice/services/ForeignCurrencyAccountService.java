package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.repositories.ForeignCurrencyAccountRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForeignCurrencyAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    public ForeignCurrencyAccountService(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
    }
}
