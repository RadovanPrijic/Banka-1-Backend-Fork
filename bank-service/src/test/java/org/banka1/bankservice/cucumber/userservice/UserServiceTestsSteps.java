package org.banka1.bankservice.cucumber.userservice;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserDto;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Department;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserServiceTestsSteps {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected MockMvc mockMvc;
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private String token;
    private Long userId;
    private Long updateId;



    @Given("Postoji zaposleni")
    public void employeeExists(){
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
            assertNotNull(result);
            System.out.println("Employee ID: " + result.getId() + '\n' + "email: " + result.getEmail());
        }else{
            System.out.println("Employee ID: " + testClient.get().getId() + '\n' + "email: " + testClient.get().getEmail());
        }



    }

    @And("Zaposleni se uloguje")
    public void userLoggedIn() {
//        List<String> role = new ArrayList<>();
//        role.add("ROLE_EMPLOYEE");
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", 1L);
//        claims.put("roles", role);
//
//        token = Jwts.builder()
//                .setClaims(claims)
//                .setSubject("admin@admin.com")
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();

        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "admin@admin.com",
                                                      "password": "Admin123!"
                                                    }
                                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");

            System.out.println("TOKEN: " + token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }



    @When("Kreira klijenta preko servisa")
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
        updateId = result.getId();
    }
    @And("Azurira klijenta preko servisa")
    public void updateClient() {

        var userUpdateDto = new UserUpdateDto("Stosic", Gender.MALE, "0622495689",
                "Bulevar Kralja Aleksandra 52", "Marko1234!", List.of("ROLE_CLIENT"));

        var emailResult = userService.findUserByEmail("marko.markovic@useremail.com");
        var result = userService.updateUser(userUpdateDto,emailResult.getId());
        assertNotNull(result);
        assertEquals(emailResult.getId(),result.getId());
        assertEquals("Marko",result.getFirstName());
        assertEquals("Stosic",result.getLastName());
        assertEquals("0622495689",result.getPhoneNumber());
        assertEquals("Bulevar Kralja Aleksandra 52",result.getHomeAddress());
    }

    @Then("Trazi klijenta preko servisa")
    public void findClient() {
        var result = userService.findUserByEmail("marko.markovic@useremail.com");
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Stosic",result.getLastName());
        assertEquals("0622495689",result.getPhoneNumber());
        assertEquals("Bulevar Kralja Aleksandra 52",result.getHomeAddress());
    }


    @When("Korisnik unosi pogresne kredencijale")
    public void loggedInWithBadCredentials() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "admina@admina.com",
                                                      "password": "Admin"
                                                    }
                                                    """))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andReturn();

        } catch (Exception e) {
            fail("User failed to login");
        }
    }
    @Then("Zaposleni se ulogovao")
    public void loggedIn() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "admin@admin.com",
                                                      "password": "Admin123!"
                                                    }
                                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");

            System.out.println("TOKEN: " + token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }

    @When("Kreira korisnika")
    public void createUser() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/register")
                                    .contentType("application/json")
                                    .content(
                                            """
                                            {
                                                "firstName": "Petar",
                                                "lastName": "Petrovic",
                                                "birthDate": "29-01-1981",
                                                "gender": "MALE",
                                                "email": "petar.petrovic@useremail.com",
                                                "phoneNumber": "0651224390",
                                                "homeAddress": "Kralja Milana 34",
                                                "roles": ["ROLE_CLIENT"]
                                            }
                                            """)
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            System.out.println("RESPONSE: " +mvcResult.getResponse().getContentAsString());
//            userId = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.id");

        } catch (Exception e) {
            fail("User failed to create user");
        }
    }

    @Then("Pronadje korisnika")
    public void findUserById() {
        String url = "/api/bank/user/"+ 3;
//        System.out.println("REQUEST: "+url);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            get(url)
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to create user");
        }
    }

    @When("Azurira korisnika")
    public void updateUser() throws JsonProcessingException {
        UserUpdateDto userUpdateDto = new UserUpdateDto("Petrovic",Gender.MALE,"0651224390","Kralja Milana 34","Marko1234!",List.of("ROLE_CLIENT"));

        String body = new io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(userUpdateDto);

//        System.out.println("UPDATEID: " + updateId);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            put("/api/bank/update/"+3)
                                    .contentType("application/json")
                                    .content(body)
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail("User failed to create user");
        }
    }

    @Then("Azuriran korisnik se uloguje")
    public void updatedUserIsLoggingIn() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "petar.petrovic@useremail.com",
                                                      "password": "Marko1234!"
                                                    }
                                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");

            System.out.println(token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }


    @Then("Zaposleni izvlaci sve klijente")
    public void getAllClients() {
//        System.out.println("REQUEST: "+url);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            get("/api/bank/clients/")
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            List<UserDto> list = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {});
//            assertEquals(2,list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to create user");
        }
    }

    @Then("Zaposleni izvlaci sve klijente filtrirano")
    public void getAllClientsFiltered() {
//        System.out.println("REQUEST: "+url);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/clients_filtered")
                                    .contentType("application/json")
                                    .content("""
                                                {
                                                    "firstname": "Marko"
                                                }
                                             """)
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            List<UserDto> list = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {});
//            assertEquals(2,list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to create user");
        }
    }

    @Then("Izvlacim informacije o meni")
    public void aboutMe() {
//        System.out.println("REQUEST: "+url);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            get("/api/bank/my-profile")
                                    .contentType("application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Access-Control-Allow-Origin", "*")
                                    .header("Authorization", "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            List<UserDto> list = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {});
//            assertEquals(2,list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to create user");
        }
    }

    @When("Zaboravio sifru")
    public void forgotPassword() {
//        System.out.println("REQUEST: "+url);
        try {
            MvcResult mvcResult = mockMvc.perform(
                            get("/api/bank/forgot-password?email={email}","admin@admin.com")
                                    .contentType("application/json"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            List<UserDto> list = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {});
//            assertEquals(2,list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to create user");
        }
    }


    @Then("Resetuje sifru")
    public void resetPassword() {
//        System.out.println("REQUEST: "+url);
        Optional<BankUser> user = userRepository.findById(1L);
        StringBuilder sb = new StringBuilder();
        sb.append('{')
                .append("\"password\": \"NovaSifra123!\", \"secretKey\": \"")
                .append(user.get().getSecretKey()).append("\"}");
        String body = sb.toString();
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/reset-password/{id}",1L)
                                    .contentType("application/json")
                                    .content(body)
                                    .header("Content-Type", "application/json"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
//            List<UserDto> list = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {});
//            assertEquals(2,list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("User failed to create user");
        }
    }

    @And("Zaposleni se ponovo uloguje")
    public void loggedInAgain() {
        try {
            MvcResult mvcResult = mockMvc.perform(
                            post("/api/bank/login")
                                    .contentType("application/json")
                                    .content(
                                            """
                                                    {
                                                      "email": "admin@admin.com",
                                                      "password": "NovaSifra123!"
                                                    }
                                                    """))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.jwtToken");

            System.out.println("TOKEN: " + token);
        } catch (Exception e) {
            fail("User failed to login");
        }
    }
}
