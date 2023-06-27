package org.banka1.bankservice.services;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.dtos.user.UserDto;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.domains.entities.account.*;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Department;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.AccountMapper;
import org.banka1.bankservice.domains.mappers.UserMapper;
import org.banka1.bankservice.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.swing.text.html.Option;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService {
    private final CurrentAccountRepository currentAccountRepository;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final BusinessAccountRepository businessAccountRepository;
    private final ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private Random random = new Random();

    public AccountService(CurrentAccountRepository currentAccountRepository, ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                            BusinessAccountRepository businessAccountRepository, ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository,
                                UserRepository userRepository, CompanyRepository companyRepository) {
        this.currentAccountRepository = currentAccountRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
        this.foreignCurrencyBalanceRepository = foreignCurrencyBalanceRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public CurrentAccountDto openCurrentAccount(CurrentAccountCreateDto currentAccountCreateDto){
        // TODO Mozda dodati provere za legitimnost prosledjenih ID-ova (verovatno nece biti potrebno)
        CurrentAccount currentAccount = new CurrentAccount();

        //TODO Mozda dodati proveru da se ne poklapa sa nekim brojem racuna u bazi (iako ovo se skoro sigurno nece desiti)
        currentAccount.setAccountNumber(generateAccountNumber(11));
        currentAccount.setAccountBalance(random.nextDouble(10000));
        currentAccount.setOwnerId(currentAccountCreateDto.getOwnerId());
        currentAccount.setAccountName(currentAccountCreateDto.getAccountName());
        currentAccount.setEmployeeId(currentAccountCreateDto.getEmployeeId());
        currentAccount.setDefaultCurrencyCode("RSD");
        currentAccount.setAccountStatus(AccountStatus.ACTIVE);
        currentAccount.setCreationDate(LocalDate.now());
        currentAccount.setAccountType(currentAccountCreateDto.getAccountType());
        currentAccount.setInterestRate(currentAccountCreateDto.getInterestRate());
        currentAccount.setMaintenanceCost(currentAccountCreateDto.getMaintenanceCost());

        currentAccountRepository.save(currentAccount);

        return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
    }

    public ForeignCurrencyAccountDto openForeignCurrencyAccount(ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto){
        // TODO Mozda dodati provere za legitimnost prosledjenih ID-ova (verovatno nece biti potrebno)
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();

        //TODO Mozda dodati proveru da se ne poklapa sa nekim brojem racuna u bazi (iako ovo se skoro sigurno nece desiti)
        foreignCurrencyAccount.setAccountNumber(generateAccountNumber(11));
        foreignCurrencyAccount.setAccountBalance(random.nextDouble(10000));
        foreignCurrencyAccount.setOwnerId(foreignCurrencyAccountCreateDto.getOwnerId());
        foreignCurrencyAccount.setAccountName(foreignCurrencyAccountCreateDto.getAccountName());
        foreignCurrencyAccount.setEmployeeId(foreignCurrencyAccountCreateDto.getEmployeeId());
        foreignCurrencyAccount.setDefaultCurrencyCode(foreignCurrencyAccountCreateDto.getDefaultCurrencyCode());
        foreignCurrencyAccount.setAccountStatus(AccountStatus.ACTIVE);
        foreignCurrencyAccount.setCreationDate(LocalDate.now());
        foreignCurrencyAccount.setAccountType(foreignCurrencyAccountCreateDto.getAccountType());
        foreignCurrencyAccount.setInterestRate(foreignCurrencyAccountCreateDto.getInterestRate());
        foreignCurrencyAccount.setMaintenanceCost(foreignCurrencyAccountCreateDto.getMaintenanceCost());
        foreignCurrencyAccountRepository.saveAndFlush(foreignCurrencyAccount);

        foreignCurrencyAccountCreateDto.getForeignCurrencyBalances().forEach(foreignCurrency -> {
            ForeignCurrencyBalance foreignCurrencyBalance = new ForeignCurrencyBalance();
            foreignCurrencyBalance.setAccount(foreignCurrencyAccount);
            foreignCurrencyBalance.setForeignCurrencyCode(foreignCurrency);
            foreignCurrencyBalance.setAccountBalance(random.nextDouble(2500));
            foreignCurrencyBalanceRepository.saveAndFlush(foreignCurrencyBalance);
        });

        foreignCurrencyAccountRepository.save(foreignCurrencyAccount);

        return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
    }

    public BusinessAccountDto openBusinessAccount(BusinessAccountCreateDto businessAccountCreateDto){
        // TODO Mozda dodati provere za legitimnost prosledjenih ID-ova (verovatno nece biti potrebno)
        BusinessAccount businessAccount = new BusinessAccount();

        //TODO Mozda dodati proveru da se ne poklapa sa nekim brojem racuna u bazi (iako ovo se skoro sigurno nece desiti)
        businessAccount.setAccountNumber(generateAccountNumber(11));
        businessAccount.setAccountBalance(random.nextDouble(10000));
        businessAccount.setOwnerId(businessAccountCreateDto.getOwnerId());
        businessAccount.setAccountName(businessAccountCreateDto.getAccountName());
        businessAccount.setEmployeeId(businessAccountCreateDto.getEmployeeId());
        businessAccount.setCompanyId(businessAccountCreateDto.getCompanyId());
        businessAccount.setDefaultCurrencyCode("RSD");
        businessAccount.setAccountStatus(AccountStatus.ACTIVE);
        businessAccount.setCreationDate(LocalDate.now());

        businessAccountRepository.save(businessAccount);

        return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount);
    }

    public CurrentAccountDto findCurrentAccountById(Long id) {
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findById(id);
        return currentAccount.map(AccountMapper.INSTANCE::currentAccountToCurrentAccountDto).orElseThrow(() -> new NotFoundException("Current account has not been found."));
    }

    public ForeignCurrencyAccountDto findForeignCurrencyAccountById(Long id) {
        Optional<ForeignCurrencyAccount> foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id);
        return foreignCurrencyAccount.map(AccountMapper.INSTANCE::foreignCurrencyAccountToForeignCurrencyAccountDto).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));
    }

    public BusinessAccountDto findBusinessAccountById(Long id) {
        Optional<BusinessAccount> businessAccount = businessAccountRepository.findById(id);
        return businessAccount.map(AccountMapper.INSTANCE::businessAccountToBusinessAccountDto).orElseThrow(() -> new NotFoundException("Business account has not been found."));
    }

    public List<AccountDto> findAllAccountsForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<BankUser> user = userRepository.findByEmail(email);

        if(user.isPresent()){
            Long userId = user.get().getId();
            List<AccountDto> userAccounts = new ArrayList<>();

            userAccounts.addAll(currentAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::currentAccountToCurrentAccountDto).collect(Collectors.toList()));
            userAccounts.addAll(foreignCurrencyAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::foreignCurrencyAccountToForeignCurrencyAccountDto).collect(Collectors.toList()));
            userAccounts.addAll(businessAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::businessAccountToBusinessAccountDto).collect(Collectors.toList()));

            return userAccounts;
        }

        return null;
    }

    public CurrentAccountDto updateCurrentAccountName(Long id, String name){
        CurrentAccount currentAccount = currentAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Current account has not been found."));

        if(currentAccount.getAccountName().equalsIgnoreCase("name"))
            throw new ValidationException("You tried to enter the same account name as is already being used.");

        List<AccountDto> userAccounts = findAllAccountsForUser();
        userAccounts.forEach(account -> {
            if(account.getAccountName().equalsIgnoreCase("name"))
                throw new ValidationException("This account name has already been used for one of user's other accounts.");
        });

        currentAccount.setAccountName(name);
        currentAccountRepository.save(currentAccount);

        return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
    }

    public CurrentAccountDto updateCurrentAccountStatus(Long id){
        CurrentAccount currentAccount = currentAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Current account has not been found."));

        if(currentAccount.getAccountStatus() == AccountStatus.ACTIVE)
            currentAccount.setAccountStatus(AccountStatus.INACTIVE);
        else
            currentAccount.setAccountStatus(AccountStatus.ACTIVE);

        currentAccountRepository.save(currentAccount);

        return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
    }

    public ForeignCurrencyAccountDto updateForeignCurrencyAccountName(Long id, String name){
        ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));

        if(foreignCurrencyAccount.getAccountName().equalsIgnoreCase("name"))
            throw new ValidationException("You tried to enter the same account name as is already being used.");

        List<AccountDto> userAccounts = findAllAccountsForUser();
        userAccounts.forEach(account -> {
            if(account.getAccountName().equalsIgnoreCase("name"))
                throw new ValidationException("This account name has already been used for one of user's other accounts.");
        });

        foreignCurrencyAccount.setAccountName(name);
        foreignCurrencyAccountRepository.save(foreignCurrencyAccount);

        return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
    }

    public ForeignCurrencyAccountDto updateForeignCurrencyAccountStatus(Long id){
        ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));

        if(foreignCurrencyAccount.getAccountStatus() == AccountStatus.ACTIVE)
            foreignCurrencyAccount.setAccountStatus(AccountStatus.INACTIVE);
        else
            foreignCurrencyAccount.setAccountStatus(AccountStatus.ACTIVE);

        foreignCurrencyAccountRepository.save(foreignCurrencyAccount);

        return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
    }

    public BusinessAccountDto updateBusinessAccountName(Long id, String name){
        BusinessAccount businessAccount = businessAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Business account has not been found."));

        if(businessAccount.getAccountName().equalsIgnoreCase("name"))
            throw new ValidationException("You tried to enter the same account name as is already being used.");

        List<AccountDto> userAccounts = findAllAccountsForUser();
        userAccounts.forEach(account -> {
            if(account.getAccountName().equalsIgnoreCase("name"))
                throw new ValidationException("This account name has already been used for one of user's other accounts.");
        });

        businessAccount.setAccountName(name);
        businessAccountRepository.save(businessAccount);

        return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount);
    }

    public BusinessAccountDto updateBusinessAccountStatus(Long id){
        BusinessAccount businessAccount = businessAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Business account has not been found."));

        if(businessAccount.getAccountStatus() == AccountStatus.ACTIVE)
            businessAccount.setAccountStatus(AccountStatus.INACTIVE);
        else
            businessAccount.setAccountStatus(AccountStatus.ACTIVE);

        businessAccountRepository.save(businessAccount);

        return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount);
    }

    public static String generateAccountNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 1; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return "2650000" + sb;
    }

}
