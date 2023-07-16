package org.banka1.bankservice.cucumber.paymentservice;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
        Optional<BankUser> testClient = userRepository.findByEmail("admin1@admin.com");
        if (testClient.isEmpty()) {
            var user = BankUser.builder()
                    .firstName("Zoran")
                    .lastName("Stosic")
                    .birthDate(LocalDate.of(1981, 1, 29))
                    .gender(Gender.MALE)
                    .email("admin1@admin.com")
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
        }
        Optional<BankUser> testClient1 = userRepository.findByEmail("marko1.markovic@useremail.com");
        if (testClient1.isEmpty()) {
            var client1 = BankUser.builder()
                    .firstName("Marko")
                    .lastName("Markovic")
                    .birthDate(LocalDate.of(1990, 10, 5))
                    .gender(Gender.MALE)
                    .email("marko1.markovic@useremail.com")
                    .phoneNumber("0651678989")
                    .homeAddress("Njegoseva 25")
                    .password(passwordEncoder.encode("Markomarkovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient1 = userRepository.saveAndFlush(client1);
//            assertNotNull(resClient1);
            this.client1 = resClient1;
        }


        Optional<BankUser> testClient2 = userRepository.findByEmail("petar1.petrovic@useremail.com");
        if (testClient2.isEmpty()) {
            var client2 = BankUser.builder()
                    .firstName("Petar")
                    .lastName("Petrovic")
                    .birthDate(LocalDate.of(1986, 3, 17))
                    .gender(Gender.MALE)
                    .email("petar1.petrovic@useremail.com")
                    .phoneNumber("0651224390")
                    .homeAddress("Kralja Milana 34")
                    .password(passwordEncoder.encode("Petarpetrovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient2 = userRepository.saveAndFlush(client2);
//            assertNotNull(resClient2);
            this.client2 = resClient2;
        }


        Optional<BankUser> testClient3 = userRepository.findByEmail("jovana1.jovanovic@useremail.com");
        if (testClient3.isEmpty()) {
            var client3 = BankUser.builder()
                    .firstName("Jovana")
                    .lastName("Jovanovic")
                    .birthDate(LocalDate.of(1988, 9, 11))
                    .gender(Gender.FEMALE)
                    .email("jovana1.jovanovic@useremail.com")
                    .phoneNumber("0633456751")
                    .homeAddress("Budimska 12")
                    .password(passwordEncoder.encode("Jovanajovanovic123!"))
                    .roles(List.of("ROLE_CLIENT"))
                    .build();

            var resClient3 = userRepository.saveAndFlush(client3);
//            assertNotNull(resClient3);
            this.client3 = resClient3;
        }

    }

    public void userIsLoggingIn(BankUser loggingUser){

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


//        String body = "{\"email\": \""+loggingUser.getEmail()+"\",\"password\": \"Markomarkovic123!\"}";
//        System.out.println("BODY: " + body);
//        try {
//            MvcResult mvcResult = mockMvc.perform(
//                            post("/api/bank/login")
//                                    .contentType("application/json")
//                                    .content(body))
//                    .andDo(print())
//                    .andExpect(status().isOk())
//                    .andReturn();
//            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");
//
//            System.out.println("TOKEN: " + token);
//
//            UserDetails userDetails = new User(loggingUser.getEmail(), loggingUser.getPassword(), loggingUser.getAuthorities());
//            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//            usernamePasswordAuthenticationToken
//                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(mvcResult.getRequest()));
//
//            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("User failed to login");
//        }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", loggingUser.getId());
            claims.put("roles", loggingUser.getRoles());

            token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(loggingUser.getEmail())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();

        UserDetails userDetails = new User(loggingUser.getEmail(), loggingUser.getPassword(), loggingUser.getAuthorities());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

//        usernamePasswordAuthenticationToken
//                .setDetails(new WebAuthenticationDetailsSource().buildDetails(mvcResult.getRequest()));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
//    public void firstEmployeeIsLoggingIn(BankUser loggingUser){
////        String body = "{\"email\": \""+loggingUser.getEmail()+"\",\"password\": \"Admin123!\"}";
////        System.out.println("BODY: " + body);
////        try {
////            MvcResult mvcResult = mockMvc.perform(
////                            post("/api/bank/login")
////                                    .contentType("application/json")
////                                    .content(body))
////                    .andDo(print())
////                    .andExpect(status().isOk())
////                    .andReturn();
////            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");
////
////            System.out.println("TOKEN: " + token);
////
////            UserDetails userDetails = new User(loggingUser.getEmail(), loggingUser.getPassword(), loggingUser.getAuthorities());
////            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
////                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////
////            usernamePasswordAuthenticationToken
////                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(mvcResult.getRequest()));
////
////            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
////        } catch (Exception e) {
////            e.printStackTrace();
////            fail("User failed to login");
////        }
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", loggingUser.getId());
//        claims.put("roles", loggingUser.getRoles());
//
//        token = Jwts.builder()
//                .setClaims(claims)
//                .setSubject(loggingUser.getEmail())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();
//
//        UserDetails userDetails = new User(loggingUser.getEmail(), loggingUser.getPassword(), loggingUser.getAuthorities());
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
////        usernamePasswordAuthenticationToken
////                .setDetails(new WebAuthenticationDetailsSource().buildDetails(mvcResult.getRequest()));
//
//        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//    }

    @Given("Zaposleni se uloguje i otvara Marku dva tekuca racuna")
    public void two_clients_open_current_account(){

        Optional<BankUser> optEmployee = userRepository.findByEmail("admin1@admin.com");
        assertNotNull(optEmployee.get());
        userIsLoggingIn(optEmployee.get());
        BankUser employee = optEmployee.get();
        assertEquals("admin1@admin.com",SecurityContextHolder.getContext().getAuthentication().getName());

        Optional<BankUser> optClient = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(optClient.get());
        BankUser client = optClient.get();

        CurrentAccountCreateDto currentAccountCreateDto =
                new CurrentAccountCreateDto(client.getId(),"My first saving account",employee.getId(), AccountType.SAVINGS,5D,1000D);
        var result1 = accountService.openCurrentAccount(currentAccountCreateDto);
        assertNotNull(result1);
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),result1.getAccountBalance(),"subtraction","RSD");
        paymentService.changeAccountBalance("CURRENT",result1.getAccountNumber(),50000D,"addition","RSD");
        var res1 = accountService.findCurrentAccountById(result1.getId());
        assertEquals(50000D,res1.getAccountBalance());

        currentAcc1 = res1;

        CurrentAccountCreateDto currentAccountCreateDto1 =
                new CurrentAccountCreateDto(client.getId(),"My second saving account",employee.getId(), AccountType.SAVINGS,5D,1000D);
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

    @When("Marko se uloguje i radi transfer iz prvog tekuceg racuna u drugi tekuci racun")
    public void marko_does_transfer_payment(){
        Optional<BankUser> client = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(client.get());
        userIsLoggingIn(client.get());
        assertEquals("marko1.markovic@useremail.com",SecurityContextHolder.getContext().getAuthentication().getName());

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

    @Given("Zaposleni se uloguje i otvara Marku devizni racun jedan za EUR drugi za EUR")
    public void marko_makes_foreign_account(){
        Optional<BankUser> optEmployee = userRepository.findByEmail("admin1@admin.com");
        assertNotNull(optEmployee.get());
        userIsLoggingIn(optEmployee.get());
        BankUser employee = optEmployee.get();
        assertEquals("admin1@admin.com",SecurityContextHolder.getContext().getAuthentication().getName());

        Optional<BankUser> optClient = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(optClient.get());
        BankUser client = optClient.get();

        ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto =
                new ForeignCurrencyAccountCreateDto(client.getId(),"My first foreign account",employee.getId()
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
                new ForeignCurrencyAccountCreateDto(client.getId(),"My second foreign account",employee.getId()
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

    @When("Marko se uloguje i radi transfer iz prvog deviznog racuna u drugi devizni racun")
    public void marko_does_foreign_transfer_payment(){
        Optional<BankUser> user = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(user.get());
        userIsLoggingIn(user.get());

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
    @Given("Zaposleni pravi dve kompanije Mirko.doo i Rajko.doo respektivno")
    public void two_companies_are_made(){
        Optional<BankUser> optEmployee = userRepository.findByEmail("admin1@admin.com");
        assertNotNull(optEmployee.get());
        userIsLoggingIn(optEmployee.get());
        BankUser employee = optEmployee.get();
//        assertEquals("marko1.markovic@useremail.com",SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<BankUser> optClient = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(optClient.get());
        BankUser client = optClient.get();

        BusinessAccountCreateDto businessAccountCreateDto =
                new BusinessAccountCreateDto(client.getId(),"Mirko.doo", employee.getId(),1L);
        var result = accountService.openBusinessAccount(businessAccountCreateDto);
        assertNotNull(result);
        company1 = result;

        Optional<BankUser> optClient1 = userRepository.findByEmail("petar1.petrovic@useremail.com");
        assertNotNull(optClient1.get());
        BankUser client1 = optClient1.get();

        BusinessAccountCreateDto businessAccountCreateDto1 =
                new BusinessAccountCreateDto(client.getId(),"Rajko.doo", employee.getId(),2L);
        var result1 = accountService.openBusinessAccount(businessAccountCreateDto1);
        assertNotNull(result1);
        company2 = result1;
    }

    @When("Marko,Vlasnik kompanije Mirko.doo, se uloguje i uplacuje iznos novca kompaniji Rajko.doo")
    public void first_company_pays_to_second_company(){
        Optional<BankUser> user = userRepository.findByEmail("marko1.markovic@useremail.com");
        assertNotNull(user.get());
        userIsLoggingIn(user.get());

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
