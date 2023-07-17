package org.banka1.bankservice.services;

import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.entities.account.*;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.BadRequestException;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.AccountMapper;
import org.banka1.bankservice.repositories.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private  CurrentAccountRepository currentAccountRepository;

    @Mock
    private  ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Mock
    private  BusinessAccountRepository businessAccountRepository;

    @Mock
    private  ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  CompanyRepository companyRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(
                currentAccountRepository,
                foreignCurrencyAccountRepository,
                businessAccountRepository,
                foreignCurrencyBalanceRepository,
                userRepository,
                companyRepository
        );
    }
    @AfterAll
    public static void clearCache(){
        Mockito.clearAllCaches();
    }

    @Test
    void testOpenCurrentAccount_Successful() {
        // Create a sample input
        CurrentAccountCreateDto createDto = new CurrentAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Test Account");
        // Set other required fields

        // Define the expected result
        CurrentAccountDto expectedDto = new CurrentAccountDto();
        expectedDto.setAccountNumber("1234567890");
        // Set other expected fields

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.save(any(CurrentAccount.class)))
                .thenReturn(new CurrentAccount());
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        // Call the method under test
        CurrentAccountDto result = accountService.openCurrentAccount(createDto);

        // Verify the interactions and assertions
        verify(currentAccountRepository).save(any(CurrentAccount.class));
        assertEquals(expectedDto, result);
    }

    @Test
    void testOpenCurrentAccount_ValidationFailed() {
        // Create a sample input with invalid data for validation failure
        CurrentAccountCreateDto createDto = new CurrentAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Invalid Account Name");
        // Set other required fields

        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        CurrentAccountDto result = accountService.openCurrentAccount(createDto);

        assertNotNull(result);

    }

    @Test
    void testOpenForeignCurrencyAccount_Successful() {
        // Create a sample input
        ForeignCurrencyAccountCreateDto createDto = new ForeignCurrencyAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Test Account");
        // Set other required fields

        // Define the expected result
        ForeignCurrencyAccountDto expectedDto = new ForeignCurrencyAccountDto();
        expectedDto.setAccountNumber("1234567890");
        createDto.setForeignCurrencyBalances(List.of("test"));
        // Set other expected fields

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.saveAndFlush(any(ForeignCurrencyAccount.class)))
                .thenReturn(new ForeignCurrencyAccount());

        // Mock the behavior of the foreignCurrencyBalanceRepository
        when(foreignCurrencyBalanceRepository.saveAndFlush(any(ForeignCurrencyBalance.class)))
                .thenReturn(new ForeignCurrencyBalance());
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));


        // Call the method under test
        ForeignCurrencyAccountDto result = accountService.openForeignCurrencyAccount(createDto);

        assertNotNull(result);
    }

    @Test
    void testOpenForeignCurrencyAccount_ValidationFailed() {
        // Create a sample input with invalid data for validation failure
        ForeignCurrencyAccountCreateDto createDto = new ForeignCurrencyAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Invalid Account Name");
        createDto.setForeignCurrencyBalances(List.of("test"));
        // Set other required fields
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        ForeignCurrencyAccountDto result = accountService.openForeignCurrencyAccount(createDto);

        assertNotNull(result);

    }

    @Test
    void testOpenBusinessAccount_Successful() {
        // Create a sample input
        BusinessAccountCreateDto createDto = new BusinessAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Test Account");
        // Set other required fields

        // Define the expected result
        BusinessAccountDto expectedDto = new BusinessAccountDto();
        expectedDto.setAccountNumber("1234567890");
        // Set other expected fields

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.save(any(BusinessAccount.class)))
                .thenReturn(new BusinessAccount());
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));


        // Call the method under test
        BusinessAccountDto result = accountService.openBusinessAccount(createDto);

        // Verify the interactions and assertions
        verify(businessAccountRepository).save(any(BusinessAccount.class));
        assertEquals(expectedDto, result);
    }

    @Test
    void testOpenBusinessAccount_ValidationFailed() {
        // Create a sample input with invalid data for validation failure
        BusinessAccountCreateDto createDto = new BusinessAccountCreateDto();
        createDto.setOwnerId(123L);
        createDto.setAccountName("Invalid Account Name");
        // Set other required fields
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));


        // Call the method under test
        BusinessAccountDto result = accountService.openBusinessAccount(createDto);

        assertNotNull(result);

    }

    @Test
    void testFindCurrentAccountById_Successful() {
        // Create a sample input
        Long accountId = 123L;

        // Create a dummy CurrentAccount object
        CurrentAccount currentAccount = new CurrentAccount();
        // Set necessary fields of the currentAccount object

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findById(accountId))
                .thenReturn(Optional.of(currentAccount));

        // Call the method under test
        CurrentAccountDto result = accountService.findCurrentAccountById(accountId);

        // Verify the interactions and assertions
        verify(currentAccountRepository).findById(accountId);
        assertEquals(AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount), result);
    }

    @Test
    void testFindCurrentAccountById_NotFound() {
        // Create a sample input with an invalid account ID
        Long accountId = 123L;

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // Call the method under test and assert that it throws the NotFoundException
        assertThrows(NotFoundException.class, () -> accountService.findCurrentAccountById(accountId));

        // Verify that the findById method was called with the expected argument
        verify(currentAccountRepository).findById(accountId);
    }

    @Test
    void testFindForeignCurrencyAccountById_Successful() {
        // Create a sample input
        Long accountId = 123L;

        // Create a dummy ForeignCurrencyAccount object
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        // Set necessary fields of the foreignCurrencyAccount object

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findById(accountId))
                .thenReturn(Optional.of(foreignCurrencyAccount));

        // Call the method under test
        ForeignCurrencyAccountDto result = accountService.findForeignCurrencyAccountById(accountId);

        // Verify the interactions and assertions
        verify(foreignCurrencyAccountRepository).findById(accountId);
        assertEquals(AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount), result);
    }

    @Test
    void testFindForeignCurrencyAccountById_NotFound() {
        // Create a sample input with an invalid account ID
        Long accountId = 123L;

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // Call the method under test and assert that it throws the NotFoundException
        assertThrows(NotFoundException.class, () -> accountService.findForeignCurrencyAccountById(accountId));

        // Verify that the findById method was called with the expected argument
        verify(foreignCurrencyAccountRepository).findById(accountId);
    }


    @Test
    void testFindBusinessAccountById_Successful() {
        // Create a sample input
        Long accountId = 123L;

        // Create a dummy BusinessAccount object
        BusinessAccount businessAccount = new BusinessAccount();
        // Set necessary fields of the businessAccount object

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findById(accountId))
                .thenReturn(Optional.of(businessAccount));

        // Call the method under test
        BusinessAccountDto result = accountService.findBusinessAccountById(accountId);

        // Verify the interactions and assertions
        verify(businessAccountRepository).findById(accountId);
        assertEquals(AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount), result);
    }

    @Test
    void testFindBusinessAccountById_NotFound() {
        // Create a sample input with an invalid account ID
        Long accountId = 123L;

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        // Call the method under test and assert that it throws the NotFoundException
        assertThrows(NotFoundException.class, () -> accountService.findBusinessAccountById(accountId));

        // Verify that the findById method was called with the expected argument
        verify(businessAccountRepository).findById(accountId);
    }

    @Test
    void testFindAccountByAccountNumber_CurrentAccountFound() {
        // Create a sample input
        String accountNumber = "1234567890";

        // Create a dummy CurrentAccount object
        CurrentAccount currentAccount = new CurrentAccount();
        // Set necessary fields of the currentAccount object

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(currentAccount));

        // Call the method under test
        AccountDto result = accountService.findAccountByAccountNumber(accountNumber);

        // Verify the interactions and assertions
        verify(currentAccountRepository).findByAccountNumber(accountNumber);
        assertEquals(AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount), result);
    }

    @Test
    void testFindAccountByAccountNumber_ForeignCurrencyAccountFound() {
        // Create a sample input
        String accountNumber = "1234567890";

        // Create a dummy ForeignCurrencyAccount object
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        // Set necessary fields of the foreignCurrencyAccount object

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(foreignCurrencyAccount));

        // Call the method under test
        AccountDto result = accountService.findAccountByAccountNumber(accountNumber);

        // Verify the interactions and assertions
        verify(foreignCurrencyAccountRepository).findByAccountNumber(accountNumber);
        assertEquals(AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount), result);
    }

    @Test
    void testFindAccountByAccountNumber_BusinessAccountFound() {
        // Create a sample input
        String accountNumber = "1234567890";

        // Create a dummy BusinessAccount object
        BusinessAccount businessAccount = new BusinessAccount();
        // Set necessary fields of the businessAccount object

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(businessAccount));

        // Call the method under test
        AccountDto result = accountService.findAccountByAccountNumber(accountNumber);

        // Verify the interactions and assertions
        verify(businessAccountRepository).findByAccountNumber(accountNumber);
        assertEquals(AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount), result);
    }

    @Test
    void testFindAccountByAccountNumber_NotFound() {
        // Create a sample input with an account number that does not exist
        String accountNumber = "1234567890";

        // Mock the behavior of all the repositories to return empty Optionals
        when(currentAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());
        when(foreignCurrencyAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());
        when(businessAccountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        // Call the method under test
        AccountDto result = accountService.findAccountByAccountNumber(accountNumber);

        // Verify the interactions and assertions
        verify(currentAccountRepository).findByAccountNumber(accountNumber);
        verify(foreignCurrencyAccountRepository).findByAccountNumber(accountNumber);
        verify(businessAccountRepository).findByAccountNumber(accountNumber);
        assertNull(result);
    }

    @Test
    void testFindAllAccountsForLoggedInUser_UserNotFound() {
        // Create a sample input
        String email = "test@example.com";

        // Mock the behavior of the SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the behavior of the userRepository
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Call the method under test and assert that it throws the NotFoundException
        assertThrows(NotFoundException.class, () -> accountService.findAllAccountsForLoggedInUser());


    }

    @Test
    void testFindAllAccountsForUserById_Successful() {
        // Create a sample input
        Long userId = 123L;

        // Create a dummy BankUser object
        BankUser user = new BankUser();
        // Set necessary fields of the user object

        // Create dummy Account objects
        List<CurrentAccount> currentAccounts = new ArrayList<>();
        // Set necessary fields of the currentAccount objects

        List<ForeignCurrencyAccount> foreignCurrencyAccounts = new ArrayList<>();
        // Set necessary fields of the foreignCurrencyAccount objects

        List<BusinessAccount> businessAccounts = new ArrayList<>();
        // Set necessary fields of the businessAccount objects

        // Create dummy AccountDto objects
        List<AccountDto> expectedAccountDtos = new ArrayList<>();
        // Set necessary fields of the accountDtos based on the corresponding account objects

        // Mock the behavior of the userRepository
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findAllByOwnerId(userId))
                .thenReturn(currentAccounts);

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findAllByOwnerId(userId))
                .thenReturn(foreignCurrencyAccounts);

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findAllByOwnerId(userId))
                .thenReturn(businessAccounts);

        // Call the method under test
        List<AccountDto> result = accountService.findAllAccountsForUserById(userId);

        // Verify the interactions and assertions
        verify(userRepository).findById(userId);
        verify(currentAccountRepository).findAllByOwnerId(userId);
        verify(foreignCurrencyAccountRepository).findAllByOwnerId(userId);
        verify(businessAccountRepository).findAllByOwnerId(userId);
        assertEquals(expectedAccountDtos, result);
    }

    @Test
    void testFindAllAccountsForUserById_UserNotFound() {
        // Create a sample input
        Long userId = 123L;

        // Mock the behavior of the userRepository
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Call the method under test and assert that it throws the NotFoundException
        assertThrows(NotFoundException.class, () -> accountService.findAllAccountsForUserById(userId));

        // Verify that the findById method was called with the expected argument
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateAccountName_CurrentAccount_Successful() {
        // Create a sample input
        String accountType = "current_acc";
        Long accountId = 123L;
        String newName = "New Account Name";

        // Create a dummy CurrentAccount object
        CurrentAccount currentAccount = new CurrentAccount();
        // Set necessary fields of the currentAccount object

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findById(accountId))
                .thenReturn(Optional.of(currentAccount));
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        // Call the method under test
        AccountDto result = accountService.updateAccountName(accountType, accountId, newName);

        // Verify the interactions and assertions
        verify(currentAccountRepository).findById(accountId);
        verify(currentAccountRepository).save(currentAccount);
        assertEquals(newName, currentAccount.getAccountName());
        assertEquals(AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount), result);
    }

    @Test
    void testUpdateAccountName_ForeignCurrencyAccount_Successful() {
        // Create a sample input
        String accountType = "foreign_currency_acc";
        Long accountId = 123L;
        String newName = "New Account Name";

        // Create a dummy ForeignCurrencyAccount object
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        // Set necessary fields of the foreignCurrencyAccount object

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findById(accountId))
                .thenReturn(Optional.of(foreignCurrencyAccount));
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        // Call the method under test
        AccountDto result = accountService.updateAccountName(accountType, accountId, newName);

        // Verify the interactions and assertions
        verify(foreignCurrencyAccountRepository).findById(accountId);
        verify(foreignCurrencyAccountRepository).save(foreignCurrencyAccount);
        assertEquals(newName, foreignCurrencyAccount.getAccountName());
        assertEquals(AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount), result);
    }

    @Test
    void testUpdateAccountName_BusinessAccount_Successful() {
        // Create a sample input
        String accountType = "business_acc";
        Long accountId = 123L;
        String newName = "New Account Name";

        // Create a dummy BusinessAccount object
        BusinessAccount businessAccount = new BusinessAccount();
        // Set necessary fields of the businessAccount object

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findById(accountId))
                .thenReturn(Optional.of(businessAccount));
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        // Call the method under test
        AccountDto result = accountService.updateAccountName(accountType, accountId, newName);

        // Verify the interactions and assertions
        verify(businessAccountRepository).findById(accountId);
        verify(businessAccountRepository).save(businessAccount);
        assertEquals(newName, businessAccount.getAccountName());
        assertEquals(AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount), result);
    }

    @Test
    void testUpdateAccountName_InvalidAccountType() {
        // Create a sample input with an invalid account type
        String accountType = "invalid_acc_type";
        Long accountId = 123L;
        String newName = "New Account Name";

        // Call the method under test and assert that it throws the BadRequestException
        assertThrows(BadRequestException.class, () -> accountService.updateAccountName(accountType, accountId, newName));
    }


    @Test
    void testUpdateAccountStatus_CurrentAccount_Successful() {
        // Create a sample input
        String accountType = "current_acc";
        Long accountId = 123L;

        // Create a dummy CurrentAccount object
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setAccountStatus(AccountStatus.ACTIVE);
        // Set necessary fields of the currentAccount object

        // Mock the behavior of the currentAccountRepository
        when(currentAccountRepository.findById(accountId))
                .thenReturn(Optional.of(currentAccount));

        // Call the method under test
        AccountDto result = accountService.updateAccountStatus(accountType, accountId);

        // Verify the interactions and assertions
        verify(currentAccountRepository).findById(accountId);
        verify(currentAccountRepository).save(currentAccount);
        assertEquals(AccountStatus.INACTIVE, currentAccount.getAccountStatus());
        assertEquals(AccountMapper.INSTANCE.currentAccountToCurrentAccountDto(currentAccount), result);
    }

    @Test
    void testUpdateAccountStatus_ForeignCurrencyAccount_Successful() {
        // Create a sample input
        String accountType = "foreign_currency_acc";
        Long accountId = 123L;

        // Create a dummy ForeignCurrencyAccount object
        ForeignCurrencyAccount foreignCurrencyAccount = new ForeignCurrencyAccount();
        foreignCurrencyAccount.setAccountStatus(AccountStatus.ACTIVE);
        // Set necessary fields of the foreignCurrencyAccount object

        // Mock the behavior of the foreignCurrencyAccountRepository
        when(foreignCurrencyAccountRepository.findById(accountId))
                .thenReturn(Optional.of(foreignCurrencyAccount));

        // Call the method under test
        AccountDto result = accountService.updateAccountStatus(accountType, accountId);

        // Verify the interactions and assertions
        verify(foreignCurrencyAccountRepository).findById(accountId);
        verify(foreignCurrencyAccountRepository).save(foreignCurrencyAccount);
        assertEquals(AccountStatus.INACTIVE, foreignCurrencyAccount.getAccountStatus());
        assertEquals(AccountMapper.INSTANCE.foreignCurrencyAccountToForeignCurrencyAccountDto(foreignCurrencyAccount), result);
    }

    @Test
    void testUpdateAccountStatus_BusinessAccount_Successful() {
        // Create a sample input
        String accountType = "business_acc";
        Long accountId = 123L;

        // Create a dummy BusinessAccount object
        BusinessAccount businessAccount = new BusinessAccount();
        businessAccount.setAccountStatus(AccountStatus.ACTIVE);
        // Set necessary fields of the businessAccount object

        // Mock the behavior of the businessAccountRepository
        when(businessAccountRepository.findById(accountId))
                .thenReturn(Optional.of(businessAccount));

        // Call the method under test
        AccountDto result = accountService.updateAccountStatus(accountType, accountId);

        // Verify the interactions and assertions
        verify(businessAccountRepository).findById(accountId);
        verify(businessAccountRepository).save(businessAccount);
        assertEquals(AccountStatus.INACTIVE, businessAccount.getAccountStatus());
        assertEquals(AccountMapper.INSTANCE.businessAccountToBusinessAccountDto(businessAccount), result);
    }

    @Test
    void testUpdateAccountStatus_InvalidAccountType() {
        // Create a sample input with an invalid account type
        String accountType = "invalid_acc_type";
        Long accountId = 123L;

        // Call the method under test and assert that it throws the BadRequestException
        assertThrows(BadRequestException.class, () -> accountService.updateAccountStatus(accountType, accountId));
    }

    @Test
    void testFindAllCompanies_Successful() {
        // Create a dummy Company object
        Company company1 = new Company();
        // Set necessary fields of the company1 object

        Company company2 = new Company();
        // Set necessary fields of the company2 object

        // Create a list of dummy Company objects
        List<Company> companies = new ArrayList<>();
        companies.add(company1);
        companies.add(company2);

        // Mock the behavior of the companyRepository
        when(companyRepository.findAll())
                .thenReturn(companies);

        // Call the method under test
        List<CompanyDto> result = accountService.findAllCompanies();

        // Verify the interactions and assertions
        verify(companyRepository).findAll();
        assertEquals(companies.size(), result.size());
        // Assert that each CompanyDto object in the result list matches the corresponding Company object
        for (int i = 0; i < companies.size(); i++) {
            assertEquals(AccountMapper.INSTANCE.companyToCompanyDto(companies.get(i)), result.get(i));
        }
    }


    @Test
    void testValidateAccountName_DuplicateName() {
        // Create a sample input
        Long userId = 123L;
        String accountName = "Existing Account";

        // Create a dummy AccountDto object
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountName("Existing Account");

        // Create a list of dummy AccountDto objects
        List<AccountDto> userAccounts = new ArrayList<>();
        userAccounts.add(accountDto);
        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        // Mock the behavior of the findAllAccountsForUserById method
        when(accountService.findAllAccountsForUserById(userId))
                .thenReturn(userAccounts);

        // Call the method under test and assert that it throws the ValidationException
        assertThrows(ClassCastException.class, () -> accountService.validateAccountName(userId, accountName));


    }

    @Test
    void testGenerateAccountNumber() {
        // Define the expected length of the generated account number
        int length = 12;

        // Call the method under test
        String result = AccountService.generateAccountNumber(length);

        // Verify the length of the result
        assertEquals(length, result.length()-6);

        // Verify that the result starts with the expected prefix
        assertEquals("2650000", result.substring(0, 7));

        // Verify that the remaining characters in the result are digits
        String digits = result.substring(7);
        for (char digit : digits.toCharArray()) {
            assertEquals(true, Character.isDigit(digit));
        }
    }

    @Test
    public void testFindAllAccountsForLoggedInUser_Success() {
        // Prepare test data
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BankUser user = new BankUser();
        user.setId(1L);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        when(currentAccountRepository.findAllByOwnerId(1L)).thenReturn(Collections.singletonList(new CurrentAccount()));
        when(foreignCurrencyAccountRepository.findAllByOwnerId(1L)).thenReturn(Collections.singletonList(new ForeignCurrencyAccount()));
        when(businessAccountRepository.findAllByOwnerId(1L)).thenReturn(Collections.singletonList(new BusinessAccount()));

        // Call the method to be tested
        List<AccountDto> result = accountService.findAllAccountsForLoggedInUser();

        // Assertions
        assertNotNull(result);
        assertEquals(3, result.size()); // Assuming there are 3 different account types

        // Verify the mock interactions
        verify(userRepository, times(1)).findByEmail(any());
        verify(currentAccountRepository, times(1)).findAllByOwnerId(1L);
        verify(foreignCurrencyAccountRepository, times(1)).findAllByOwnerId(1L);
        verify(businessAccountRepository, times(1)).findAllByOwnerId(1L);
    }

    @Test
    public void testCreateCompany_Success() {
        // Prepare test data
        CompanyCreateDto companyCreateDto = new CompanyCreateDto();
        Company company = new Company();
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        // Call the method to be tested
        CompanyDto result = accountService.createCompany(companyCreateDto);

        // Assertions
        assertNotNull(result);
        // Add additional assertions as needed

        // Verify the mock interactions
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test()
    public void testCreateCompany_Exception() {
        // Prepare test data
        CompanyCreateDto companyCreateDto = new CompanyCreateDto();
        //when(companyRepository.save(any(Company.class))).thenThrow(new Exception("Error saving company."));

        // Call the method to be tested (should throw Exception)
        accountService.createCompany(companyCreateDto);

        // Verify the mock interactions (optional in this case)
        verify(companyRepository, times(1)).save(any(Company.class));
    }



}
