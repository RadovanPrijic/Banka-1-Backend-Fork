package org.banka1.bankservice.cucumber.userservice;

import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.bankservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserServiceTestsSteps {
    @Autowired
    private UserService userService;
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


}
