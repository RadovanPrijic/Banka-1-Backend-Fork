package org.banka1.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase
@Transactional
@TestPropertySource(value = "/application-test_it.properties")
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

    @Autowired
    protected ObjectMapper objectMapper;

    protected String token;
    private boolean isInitialized;

    @BeforeEach
    public void beforeEach() {
        if (isInitialized) return;
        isInitialized = true;

        initUsers();
        initToken();
    }

    private void initUsers() {
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("test@test.com")
                .position(Position.ADMINISTRATOR)
                .phoneNumber("111222333")
                .password(passwordEncoder.encode("test1234"))
                .roles(List.of(User.USER_ADMIN))
                .build();

        User user1 = User.builder()
                .firstName("User1")
                .lastName("User1")
                .email("user1@user1.com")
                .position(Position.EMPLOYEE)
                .jmbg("2222222222")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user1"))
                .roles(List.of(User.USER_MODERATOR))
                .active(true)
                .build();

        User user2 = User.builder()
                .firstName("User2")
                .lastName("User2")
                .email("user2@user2.com")
                .position(Position.EMPLOYEE)
                .jmbg("3333333333")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user3"))
                .roles(List.of(User.USER_MODERATOR))
                .active(true)
                .build();

        User user3 = User.builder()
                .firstName("User3")
                .lastName("User3")
                .email("user3@user3.com")
                .position(Position.EMPLOYEE)
                .jmbg("4444444444")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user3"))
                .roles(List.of(User.USER_MODERATOR))
                .active(true)
                .build();

        userRepository.saveAll(List.of(admin, user1, user2, user3));
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
