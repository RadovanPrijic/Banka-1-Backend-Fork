package org.banka1.bankservice.cucumber.paymentservice;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.dtos.payment.MoneyTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentCreateDto;
import org.banka1.bankservice.domains.entities.account.AccountType;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private BankUser employee1;
    private BankUser client1;
    private BankUser client2;
    private BankUser client3;
    private CurrentAccountDto currentAcc1;
    private CurrentAccountDto currentacc2;
    private ForeignCurrencyAccountDto foreignAcc1;
    private ForeignCurrencyAccountDto foreignAcc2;

    @BeforeStep
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
            this.client2 = resClient2;

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
            this.client3 = resClient3;




            //Token initialization

//            List<String> role = new ArrayList<>();
//            role.add("ROLE_EMPLOYEE");
//
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("userId", 1L);
//            claims.put("roles", role);
//
//            token = Jwts.builder()
//                    .setClaims(claims)
//                    .setSubject("admin@admin.com")
//                    .setIssuedAt(new Date(System.currentTimeMillis()))
//                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();

            System.out.println("TESTDATA SET UP");
        }else{
            System.out.println("Test data is already initialized");
        }
    }

    public void firstClientIsLoggingIn(BankUser loggingUser){

//
//
//
//        List<String> role = loggingUser.getRoles();
////        role.add("ROLE_EMPLOYEE");
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", loggingUser.getId());
//        claims.put("roles", role);
//
//        token = Jwts.builder()
//                .setClaims(claims)
//                .setSubject(loggingUser.getEmail())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();



        String body = "{\"email\": \""+loggingUser.getEmail()+"\",\"password\": \"Markomarkovic123!\"}";
        System.out.println("BODY: " + body);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(body))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");

            System.out.println("TOKEN: " + token);

            UserDetails userDetails = new User(loggingUser.getEmail(), loggingUser.getPassword(), loggingUser.getAuthorities());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(mvcResult.getRequest()));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to login");
        }
    }

    @Given("Harold je otvorio dva tekuca racuna i Harold se ulogovao")
    public void two_clients_open_current_account(){

        Optional<BankUser> user = userRepository.findByEmail("marko.markovic@useremail.com");
        assertNotNull(user.get());
        firstClientIsLoggingIn(user.get());
//        assertEquals("marko.markovic@useremail.com",SecurityContextHolder.getContext().getAuthentication().getName());


        CurrentAccountCreateDto currentAccountCreateDto =
                new CurrentAccountCreateDto(2L,"My first saving account",1L, AccountType.SAVINGS,5D,1000D);
        var result1 = accountService.openCurrentAccount(currentAccountCreateDto);
        assertNotNull(result1);
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),result1.getAccountBalance(),"subtraction","RSD");
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),50000D,"addition","RSD");
        var res1 = accountService.findCurrentAccountById(result1.getId());
        assertEquals(50000D,res1.getAccountBalance());

        currentAcc1 = res1;

        CurrentAccountCreateDto currentAccountCreateDto1 =
                new CurrentAccountCreateDto(2L,"My second saving account",1L, AccountType.SAVINGS,5D,1000D);
        var result2 = accountService.openCurrentAccount(currentAccountCreateDto1);
        assertNotNull(result2);
        paymentService.changeAccountBalance("CURRENT",result2.getAccountNumber(),result2.getAccountBalance(),"subtraction","RSD");
        paymentService.changeAccountBalance("CURRENT",result2.getAccountNumber(),50000D,"addition","RSD");
        var res2 = accountService.findCurrentAccountById(result2.getId());
        assertEquals(50000D,res2.getAccountBalance());

        currentacc2 = res2;

//        var acc1 = accountService.findCurrentAccountById(result1.getId());
//        var acc2 = accountService.findCurrentAccountById(result2.getId());

//        assertEquals(50000D, acc1.getAccountBalance());
//        assertEquals(50000D, acc2.getAccountBalance());



    }

    @When("Harold radi transfer iz prvog u drugi racun")
    public void harold_does_transfer_payment(){

        MoneyTransferDto moneyTransferDto = new MoneyTransferDto(currentAcc1.getAccountNumber(), currentacc2.getAccountNumber(),50000D,"RSD");
        var result = paymentService.transferMoney(moneyTransferDto);
        assertNotNull(result);
        System.out.println("RESULT: " + result);
    }

    @Then("Stanje se promenilo na prvom i drugom racunu")
    public void current_account_balance_changed(){
        currentAcc1 = accountService.findCurrentAccountById(currentAcc1.getId());
        currentacc2 = accountService.findCurrentAccountById(currentacc2.getId());
        assertEquals(0D, currentAcc1.getAccountBalance());
        assertEquals(100000D, currentacc2.getAccountBalance());

    }

    @Given("Harold pravi devizni racun jedan za EUR drugi za EUR")
    public void harold_makes_foreign_account(){
        Optional<BankUser> user = userRepository.findByEmail("marko.markovic@useremail.com");
        assertNotNull(user.get());
        firstClientIsLoggingIn(user.get());
//        assertEquals("marko.markovic@useremail.com",SecurityContextHolder.getContext().getAuthentication().getName());

        ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto =
                new ForeignCurrencyAccountCreateDto(2L,"My first foreign account",1L
                        , "EUR",AccountType.FOREIGN_CURRENCY,5D,10D,List.of("EUR"));
        var result1 = accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto);
        assertNotNull(result1);
        assertNotNull(result1.getForeignCurrencyBalances());
        assertNotNull(result1.getForeignCurrencyBalances().get(0).getAccountBalance());
        assertEquals("EUR",result1.getForeignCurrencyBalances().get(0).getForeignCurrencyCode());
        assertNotNull(result1.getAccountNumber());

        paymentService.changeAccountBalance("FOREIGN_CURRENCY",result1.getAccountNumber()
                ,result1.getForeignCurrencyBalances().get(0).getAccountBalance(),"subtraction","EUR");
        paymentService.changeAccountBalance("FOREIGN_CURRENCY",result1.getAccountNumber()
                ,500D,"addition","EUR");

        var res1 = accountService.findForeignCurrencyAccountById(result1.getId());
        assertEquals(500D,res1.getForeignCurrencyBalances().get(0).getAccountBalance());
        foreignAcc1 = res1;




        ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto1 =
                new ForeignCurrencyAccountCreateDto(2L,"My second foreign account",1L
                        , "EUR",AccountType.FOREIGN_CURRENCY,5D,10D,List.of("EUR"));
        var result2 = accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto1);
        assertNotNull(result2);
        assertNotNull(result2.getForeignCurrencyBalances());
        assertNotNull(result2.getForeignCurrencyBalances().get(0).getAccountBalance());
        assertEquals("EUR",result2.getForeignCurrencyBalances().get(0).getForeignCurrencyCode());
        assertNotNull(result2.getAccountNumber());

        paymentService.changeAccountBalance("FOREIGN_CURRENCY",result2.getAccountNumber()
                ,result2.getForeignCurrencyBalances().get(0).getAccountBalance(),"subtraction","EUR");
        paymentService.changeAccountBalance("FOREIGN_CURRENCY",result2.getAccountNumber()
                ,500D,"addition","EUR");
        var res2 = accountService.findForeignCurrencyAccountById(result2.getId());
        assertEquals(500D,res2.getForeignCurrencyBalances().get(0).getAccountBalance());
        foreignAcc2 = res2;
    }

    @When("Harold radi transfer iz prvog deviznog racuna u drugi devizni racun")
    public void harold_does_foreign_transfer_payment(){

        MoneyTransferDto moneyTransferDto = new MoneyTransferDto(foreignAcc1.getAccountNumber(), foreignAcc2.getAccountNumber(),500D,"EUR");
        var result = paymentService.transferMoney(moneyTransferDto);
        assertNotNull(result);
        System.out.println("RESULT: " + result);
    }

    @Then("Stanje se promenilo na deviznim racunima")
    public void foreign_account_balance_changed(){
        foreignAcc1 = accountService.findForeignCurrencyAccountById(foreignAcc1.getId());
        foreignAcc2 = accountService.findForeignCurrencyAccountById(foreignAcc2.getId());
        assertEquals(0D, foreignAcc1.getForeignCurrencyBalances().get(0).getAccountBalance());
        assertNotEquals(0D, foreignAcc2.getForeignCurrencyBalances().get(0).getAccountBalance());

    }

    private BusinessAccountDto company1;
    private BusinessAccountDto company2;
    @Given("Postoje dve kompanije, Mirko.doo i Rajko.doo respektivno")
    public void two_companies_are_made(){
        Optional<BankUser> user = userRepository.findByEmail("marko.markovic@useremail.com");
        assertNotNull(user.get());
        firstClientIsLoggingIn(user.get());
//        assertEquals("marko.markovic@useremail.com",SecurityContextHolder.getContext().getAuthentication().getName());


        BusinessAccountCreateDto businessAccountCreateDto =
                new BusinessAccountCreateDto(2L,"Mirko.doo", 1L,1L);
        var result = accountService.openBusinessAccount(businessAccountCreateDto);
        assertNotNull(result);
        company1 = result;

        BusinessAccountCreateDto businessAccountCreateDto1 =
                new BusinessAccountCreateDto(3L,"Rajko.doo", 1L,2L);
        var result1 = accountService.openBusinessAccount(businessAccountCreateDto1);
        assertNotNull(result1);
        company2 = result1;
    }

    @When("Mirko.doo uplacuje iznos novca Rajko.doo")
    public void first_company_pays_to_second_company(){
        PaymentCreateDto paymentCreateDto =
                new PaymentCreateDto(company1.getAccountName(),company1.getAccountNumber(),company2.getAccountNumber(),company1.getAccountBalance(),"123451","123553","Uplata po hitnoj transakciji");
        var result = paymentService.makePayment(paymentCreateDto);
        assertNotNull(paymentCreateDto);
    }
    @Then("Mirko.doo ima manji iznos novca, dok Rajko.doo ima veci iznos novca")
        public void company_paid_to_another_company(){
        var result = paymentService.findPaymentById(1L);
        assertNotNull(result);
        var res1 = accountService.findBusinessAccountById(this.company1.getId());
        var res2 = accountService.findBusinessAccountById(this.company2.getId());
        assertEquals(0D,res1.getAccountBalance());
        assertEquals(company1.getAccountBalance() + company2.getAccountBalance(),res2.getAccountBalance());
    }



}
