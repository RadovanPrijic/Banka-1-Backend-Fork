package org.banka1.bankservice.cucumber.userservice;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UserServiceTestsSteps {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private String token;

    @When("Zaposleni se uloguje")
    public void userLoggedIn() {
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
    }

    @And("Kreira klijenta")
    public void createClient() {
        var client1 = UserCreateDto.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@useremail.com")
                .phoneNumber("0651678989")
                .homeAddress("Njegoseva 25")
                .roles(List.of("ROLE_CLIENT"))
                .build();

        var result = userService.createUser(client1);

        assertNotNull(result);
        assertEquals("marko.markovic@useremail.com",result.getEmail());
        assertEquals("Markovic",result.getLastName());
    }
    @And("Azurira korisnika")
    public void updateClient() {

        var userUpdateDto = new UserUpdateDto("Stosic", Gender.MALE, "0622495689",
                "Bulevar Kralja Aleksandra 52", "Admin1234!", List.of("ROLE_EMPLOYEE"));

        var emailResult = userService.findUserByEmail("marko.markovic@useremail.com");
        var result = userService.updateUser(userUpdateDto,emailResult.getId());
        assertNotNull(result);
        assertEquals(emailResult.getId(),result.getId());
        assertEquals("Stosic",result.getLastName());
        assertEquals("0622495689",result.getPhoneNumber());
        assertEquals("Bulevar Kralja Aleksandra 52",result.getHomeAddress());
    }

    @Then("Trazi korisnika")
    public void findClient() {
        var result = userService.findUserByEmail("marko.markovic@useremail.com");
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Stosic",result.getLastName());
        assertEquals("0622495689",result.getPhoneNumber());
        assertEquals("Bulevar Kralja Aleksandra 52",result.getHomeAddress());
    }



}
