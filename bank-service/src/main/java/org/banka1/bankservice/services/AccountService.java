package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.user.UserDto;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.mappers.UserMapper;
import org.banka1.bankservice.repositories.BusinessAccountRepository;
import org.banka1.bankservice.repositories.CurrentAccountRepository;
import org.banka1.bankservice.repositories.ForeignCurrencyAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

//    public UserDto findUserById(Long id) {
//        Optional<BankUser> user = userRepository.findById(id);
//        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
//    }
}
