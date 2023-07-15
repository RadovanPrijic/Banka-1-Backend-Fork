package org.banka1.bankservice.cucumber.paymentservice;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountCreateDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountCreateDto;
import org.banka1.bankservice.domains.dtos.payment.MoneyTransferDto;
import org.banka1.bankservice.domains.entities.account.AccountType;
import org.banka1.bankservice.domains.entities.account.CurrentAccount;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Department;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.repositories.PaymentRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.AccountService;
import org.banka1.bankservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentServiceTestsSteps {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private String token;

    private BankUser client1;
    private CurrentAccountDto acc1;
    private CurrentAccountDto acc2;

    @Before
    public void setUp(){
        Optional<BankUser> testClient = userRepository.findByEmail("admin@admin.com");
        if (testClient.isEmpty()) {
            var user =   BankUser.builder()
                    .firstName("Zoran")
                    .lastName("Stosic")
                    .birthDate(LocalDate.of(1981, 1, 29))
                    .gender(Gender.MALE)
                    .email("admin@admin.com")
                    .phoneNumber("0622495678")
                    .homeAddress("Bulevar Kralja Aleksandra 52")
                    .password(passwordEncoder.encode("Admin123!"))
                    .position(Position.SYSTEM_ADMIN)
                    .department(Department.IT)
                    .roles(List.of("ROLE_EMPLOYEE"))
                    .build();

            var result = userRepository.saveAndFlush(user);
//            assertNotNull(result);
            System.out.println("Employee ID: " + result.getId() + '\n' + "email: " + result.getEmail());


            var client1 = BankUser.builder()
                    .firstName("Marko")
                    .lastName("Markovic")
                    .birthDate(LocalDate.of(1990, 10, 5))
                    .gender(Gender.MALE)
                    .email("marko.markovic@useremail.com")
                    .phoneNumber("0651678989")
                    .homeAddress("Njegoseva 25")
                    .password(passwordEncoder.encode("Markomarkovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient1 = userRepository.saveAndFlush(client1);
//            assertNotNull(resClient1);
            this.client1 = resClient1;

            var client2 = BankUser.builder()
                    .firstName("Petar")
                    .lastName("Petrovic")
                    .birthDate(LocalDate.of(1986, 3, 17))
                    .gender(Gender.MALE)
                    .email("petar.petrovic@useremail.com")
                    .phoneNumber("0651224390")
                    .homeAddress("Kralja Milana 34")
                    .password(passwordEncoder.encode("Petarpetrovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient2 = userRepository.saveAndFlush(client2);
//            assertNotNull(resClient2);

            var client3 = BankUser.builder()
                    .firstName("Jovana")
                    .lastName("Jovanovic")
                    .birthDate(LocalDate.of(1988, 9, 11))
                    .gender(Gender.FEMALE)
                    .email("jovana.jovanovic@useremail.com")
                    .phoneNumber("0633456751")
                    .homeAddress("Budimska 12")
                    .password(passwordEncoder.encode("Jovanajovanovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient3 = userRepository.saveAndFlush(client3);
//            assertNotNull(resClient3);



            //Token initialization
            List<String> role = new ArrayList<>();
            role.add("ROLE_EMPLOYEE");

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", 1L);
            claims.put("roles", role);

            token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject("admin@admin.com")
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();

            System.out.println("TESTDATA SET UP");
        }else{
            System.out.println("Test data is already initialized");
        }
    }

    public void logInForUser(BankUser loggingClient){
        UserDetails userDetails = new User(loggingClient.getEmail(), loggingClient.getPassword(), loggingClient.getAuthorities());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Given("Harold je otvorio dva tekuca racuna i Harold se ulogovao")
    public void two_clients_open_current_account(){


        CurrentAccountCreateDto currentAccountCreateDto =
                new CurrentAccountCreateDto(2L,"My first saving account",1L, AccountType.SAVINGS,5D,1000D);
        var result1 = accountService.openCurrentAccount(currentAccountCreateDto);
        assertNotNull(result1);
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),result1.getAccountBalance(),"subtraction","RSD");
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),50000D,"addition","RSD");
        var res1 = accountService.findCurrentAccountById(result1.getId());
        assertEquals(50000D,res1.getAccountBalance());

        acc1 = res1;

        CurrentAccountCreateDto currentAccountCreateDto1 =
                new CurrentAccountCreateDto(2L,"My second saving account",1L, AccountType.SAVINGS,5D,1000D);
        var result2 = accountService.openCurrentAccount(currentAccountCreateDto1);
        assertNotNull(result2);
        paymentService.changeAccountBalance("CURRENT",result2.getAccountNumber(),result2.getAccountBalance(),"subtraction","RSD");
        paymentService.changeAccountBalance("CURRENT",result2.getAccountNumber(),50000D,"addition","RSD");
        var res2 = accountService.findCurrentAccountById(result2.getId());
        assertEquals(50000D,res2.getAccountBalance());

        acc2 = res2;

//        var acc1 = accountService.findCurrentAccountById(result1.getId());
//        var acc2 = accountService.findCurrentAccountById(result2.getId());

//        assertEquals(50000D, acc1.getAccountBalance());
//        assertEquals(50000D, acc2.getAccountBalance());

        logInForUser(client1);

    }

    @When("Harold radi transfer iz prvog u drugi racun")
    public void harold_does_transfer_payment(){

        MoneyTransferDto moneyTransferDto = new MoneyTransferDto(acc1.getAccountNumber(),acc2.getAccountNumber(),50000D,"RSD");
        var result = paymentService.transferMoney(moneyTransferDto);
        assertNotNull(result);
        System.out.println("RESULT: " + result);
    }

    @Then("Stanje se promenilo na prvom i drugom racunu")
    public void account_balance_changed(){
        acc1 = accountService.findCurrentAccountById(acc1.getId());
        acc2 = accountService.findCurrentAccountById(acc2.getId());
        assertEquals(0D,acc1.getAccountBalance());
        assertEquals(100000D,acc2.getAccountBalance());

    }

    @Given("Harold pravi devizni racun jedan za EUR drugi za USD")
    public void harold_makes_forreign_account(){
        ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto =
                new ForeignCurrencyAccountCreateDto(2L,"My first foreign account",1L
                        , "EUR",AccountType.FOREIGN_CURRENCY,5D,10D,new ArrayList<>());
        var result = accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto);

        ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto1 =
                new ForeignCurrencyAccountCreateDto(2L,"My first foreign account",1L
                        , "EUR",AccountType.FOREIGN_CURRENCY,5D,10D,new ArrayList<>());
        var result1 = accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto1);
    }

}
