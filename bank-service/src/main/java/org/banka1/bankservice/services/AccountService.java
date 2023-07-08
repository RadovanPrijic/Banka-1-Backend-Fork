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
import org.banka1.bankservice.domains.exceptions.BadRequestException;
import org.banka1.bankservice.domains.exceptions.ForbiddenException;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.AccountMapper;
import org.banka1.bankservice.domains.mappers.UserMapper;
import org.banka1.bankservice.repositories.*;
import org.springframework.security.access.annotation.Secured;
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
        validateAccountName(currentAccountCreateDto.getOwnerId(), currentAccountCreateDto.getAccountName());

        CurrentAccount currentAccount = new CurrentAccount();

        currentAccount.setAccountNumber(generateAccountNumber(12));
        currentAccount.setAccountBalance(random.nextDouble(500001));
        currentAccount.setOwnerId(currentAccountCreateDto.getOwnerId());
        currentAccount.setAccountName(currentAccountCreateDto.getAccountName());
        currentAccount.setEmployeeId(currentAccountCreateDto.getEmployeeId());
        currentAccount.setDefaultCurrencyCode("RSD");
        currentAccount.setAccountStatus(AccountStatus.ACTIVE);
        currentAccount.setCreationDate(LocalDate.now());
        currentAccount.setExpiryDate(LocalDate.now().plusYears(5));
        currentAccount.setAccountType(currentAccountCreateDto.getAccountType());
        currentAccount.setInterestRate(currentAccountCreateDto.getInterestRate());
        currentAccount.setMaintenanceCost(currentAccountCreateDto.getMaintenanceCost());

        currentAccountRepository.save(currentAccount);

        return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
    }

    public ForeignCurrencyAccountDto openForeignCurrencyAccount(ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto){
        validateAccountName(foreignCurrencyAccountCreateDto.getOwnerId(), foreignCurrencyAccountCreateDto.getAccountName());

        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();

        foreignCurrencyAccount.setAccountNumber(generateAccountNumber(12));
//        foreignCurrencyAccount.setAccountBalance(random.nextDouble(10001));
        foreignCurrencyAccount.setOwnerId(foreignCurrencyAccountCreateDto.getOwnerId());
        foreignCurrencyAccount.setAccountName(foreignCurrencyAccountCreateDto.getAccountName());
        foreignCurrencyAccount.setEmployeeId(foreignCurrencyAccountCreateDto.getEmployeeId());
        foreignCurrencyAccount.setDefaultCurrencyCode(foreignCurrencyAccountCreateDto.getDefaultCurrencyCode());
        foreignCurrencyAccount.setAccountStatus(AccountStatus.ACTIVE);
        foreignCurrencyAccount.setCreationDate(LocalDate.now());
        foreignCurrencyAccount.setExpiryDate(LocalDate.now().plusYears(5));
        foreignCurrencyAccount.setAccountType(foreignCurrencyAccountCreateDto.getAccountType());
        foreignCurrencyAccount.setInterestRate(foreignCurrencyAccountCreateDto.getInterestRate());
        foreignCurrencyAccount.setMaintenanceCost(foreignCurrencyAccountCreateDto.getMaintenanceCost());
        foreignCurrencyAccountRepository.saveAndFlush(foreignCurrencyAccount);

        List<ForeignCurrencyBalance> foreignCurrencyBalancesList = new ArrayList<>();

        foreignCurrencyAccountCreateDto.getForeignCurrencyBalances().forEach(foreignCurrency -> {
            ForeignCurrencyBalance foreignCurrencyBalance = new ForeignCurrencyBalance();

            foreignCurrencyBalance.setAccount(foreignCurrencyAccount);
            foreignCurrencyBalance.setForeignCurrencyCode(foreignCurrency);

            if(foreignCurrency.equalsIgnoreCase("RSD"))
                foreignCurrencyBalance.setAccountBalance(random.nextDouble(500001));
            else
                foreignCurrencyBalance.setAccountBalance(random.nextDouble(2501));

            foreignCurrencyBalanceRepository.saveAndFlush(foreignCurrencyBalance);

            foreignCurrencyBalancesList.add(foreignCurrencyBalance);
        });

        foreignCurrencyAccount.setForeignCurrencyBalances(foreignCurrencyBalancesList);
        foreignCurrencyAccountRepository.save(foreignCurrencyAccount);

        return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
    }

    public BusinessAccountDto openBusinessAccount(BusinessAccountCreateDto businessAccountCreateDto){
        validateAccountName(businessAccountCreateDto.getOwnerId(), businessAccountCreateDto.getAccountName());

        BusinessAccount businessAccount = new BusinessAccount();

        businessAccount.setAccountNumber(generateAccountNumber(12));
        businessAccount.setAccountBalance(random.nextDouble(5000001));
        businessAccount.setOwnerId(businessAccountCreateDto.getOwnerId());
        businessAccount.setAccountName(businessAccountCreateDto.getAccountName());
        businessAccount.setEmployeeId(businessAccountCreateDto.getEmployeeId());
        businessAccount.setCompanyId(businessAccountCreateDto.getCompanyId());
        businessAccount.setDefaultCurrencyCode("RSD");
        businessAccount.setAccountStatus(AccountStatus.ACTIVE);
        businessAccount.setCreationDate(LocalDate.now());
        businessAccount.setExpiryDate(LocalDate.now().plusYears(5));

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

    public AccountDto findAccountByAccountNumber(String accountNumber) {
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findByAccountNumber(accountNumber);
        Optional<ForeignCurrencyAccount> foreignCurrencyAccount = foreignCurrencyAccountRepository.findByAccountNumber(accountNumber);
        Optional<BusinessAccount> businessAccount = businessAccountRepository.findByAccountNumber(accountNumber);

        if(currentAccount.isPresent())
            return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount.get());
        else if (foreignCurrencyAccount.isPresent())
            return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount.get());
        else if (businessAccount.isPresent())
            return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount.get());
        else
            return null;
    }

    public List<AccountDto> findAllAccountsForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        Long userId = user.getId();
        List<AccountDto> userAccounts = new ArrayList<>();

        userAccounts.addAll(currentAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::currentAccountToCurrentAccountDto).collect(Collectors.toList()));
        userAccounts.addAll(foreignCurrencyAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::foreignCurrencyAccountToForeignCurrencyAccountDto).collect(Collectors.toList()));
        userAccounts.addAll(businessAccountRepository.findAllByOwnerId(userId).stream().map(AccountMapper.INSTANCE::businessAccountToBusinessAccountDto).collect(Collectors.toList()));

        return userAccounts;
    }

    public List<AccountDto> findAllAccountsForUserById(Long id) {
        BankUser user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User has not been found."));

        List<AccountDto> userAccounts = new ArrayList<>();

        userAccounts.addAll(currentAccountRepository.findAllByOwnerId(id).stream().map(AccountMapper.INSTANCE::currentAccountToCurrentAccountDto).collect(Collectors.toList()));
        userAccounts.addAll(foreignCurrencyAccountRepository.findAllByOwnerId(id).stream().map(AccountMapper.INSTANCE::foreignCurrencyAccountToForeignCurrencyAccountDto).collect(Collectors.toList()));
        userAccounts.addAll(businessAccountRepository.findAllByOwnerId(id).stream().map(AccountMapper.INSTANCE::businessAccountToBusinessAccountDto).collect(Collectors.toList()));

        return userAccounts;

    }

    public AccountDto updateAccountName(String accountType, Long id, String name){

        switch (accountType) {

            case "current_acc" -> {
                CurrentAccount currentAccount = currentAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Current account has not been found."));
                validateAccountName(currentAccount.getOwnerId(), name);
                currentAccount.setAccountName(name);
                currentAccountRepository.save(currentAccount);
                return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
            }

            case "foreign_currency_acc" -> {
                ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));
                validateAccountName(foreignCurrencyAccount.getOwnerId(), name);
                foreignCurrencyAccount.setAccountName(name);
                foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
                return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
            }

            case "business_acc" -> {
                BusinessAccount businessAccount = businessAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Business account has not been found."));
                validateAccountName(businessAccount.getOwnerId(), name);
                businessAccount.setAccountName(name);
                businessAccountRepository.save(businessAccount);
                return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount);
            }

            default -> throw new BadRequestException("Your request contains an invalid accountType parameter.");
        }
    }

    public AccountDto updateAccountStatus(String accountType, Long id){

        switch (accountType) {

            case "current_acc" -> {
                CurrentAccount currentAccount = currentAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Current account has not been found."));
                if (currentAccount.getAccountStatus() == AccountStatus.ACTIVE)
                    currentAccount.setAccountStatus(AccountStatus.INACTIVE);
                else
                    currentAccount.setAccountStatus(AccountStatus.ACTIVE);
                currentAccountRepository.save(currentAccount);
                return AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount);
            }

            case "foreign_currency_acc" -> {
                ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));
                if (foreignCurrencyAccount.getAccountStatus() == AccountStatus.ACTIVE)
                    foreignCurrencyAccount.setAccountStatus(AccountStatus.INACTIVE);
                else
                    foreignCurrencyAccount.setAccountStatus(AccountStatus.ACTIVE);
                foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
                return AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount);
            }

            case "business_acc" -> {
                BusinessAccount businessAccount = businessAccountRepository.findById(id).orElseThrow(() -> new NotFoundException("Business account has not been found."));
                if (businessAccount.getAccountStatus() == AccountStatus.ACTIVE)
                    businessAccount.setAccountStatus(AccountStatus.INACTIVE);
                else
                    businessAccount.setAccountStatus(AccountStatus.ACTIVE);
                businessAccountRepository.save(businessAccount);
                return AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount);
            }

            default -> throw new BadRequestException("Your request contains an invalid accountType parameter.");
        }
    }

    public List<CompanyDto> findAllCompanies() {
        return new ArrayList<>(companyRepository.findAll().stream().map(AccountMapper.INSTANCE::companyToCompanyDto).collect(Collectors.toList()));
    }

    public void validateAccountName(Long id, String name) {
        List<AccountDto> userAccounts = findAllAccountsForUserById(id);

        userAccounts.forEach(account -> {
            if(account.getAccountName().equalsIgnoreCase(name.trim()))
                throw new ValidationException("This account name has already been used for one of user's accounts.");
        });
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
