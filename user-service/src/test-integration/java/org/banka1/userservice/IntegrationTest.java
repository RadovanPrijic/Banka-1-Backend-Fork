package org.banka1.userservice;

import com.jayway.jsonpath.JsonPath;
import org.aspectj.lang.annotation.Before;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase
@Transactional
@TestPropertySource("/application-test_it.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test_it")
public abstract class IntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String token;
    private boolean isInitialized;

    @BeforeEach
    public void beforeEach() {
        if (isInitialized) return;
        isInitialized = true;

        initUser();
        initToken();
    }

    private void initUser() {
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("test@test.com")
                .position(Position.ADMINISTRATOR)
                .phoneNumber("111222333")
                .password(passwordEncoder.encode("test1234"))
                .roles(List.of(User.USER_ADMIN))
                .build();

        userRepository.save(admin);
        userRepository.flush();
    }

    public void initToken() {
        try {
            MvcResult mvcResult = mockMvc.perform(post("/api/users/login")
                            .contentType("application/json")
                            .content("""
                                    {
                                        "email": "test@test.com",
                                        "password": "test1234"
                                    }"""))
                    .andExpect(status().isOk())
                    .andReturn();

            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
