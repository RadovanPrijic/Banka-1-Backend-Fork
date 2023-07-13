package org.banka1.bankservice.controllers;

import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.services.AccountService;
import org.banka1.bankservice.services.CreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AccountController.class)
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCurrentAccountById() throws Exception {
        // Mock the accountService.findCurrentAccountById(id) method to return a sample ResponseEntity
        when(accountService.findCurrentAccountById(1L)).thenReturn(new CurrentAccountDto());

        mockMvc.perform(get("/api/bank/current_acc/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetForeignCurrencyAccountById() throws Exception {
        // Mock the accountService.findForeignCurrencyAccountById(id) method to return a sample ResponseEntity
        when(accountService.findForeignCurrencyAccountById(1L)).thenReturn(new ForeignCurrencyAccountDto());

        mockMvc.perform(get("/api/bank/foreign_currency_acc/{id}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetBusinessAccountById() throws Exception {
        // Mock the accountService.findBusinessAccountById(id) method to return a sample ResponseEntity
        when(accountService.findBusinessAccountById(1L)).thenReturn(new BusinessAccountDto());

        mockMvc.perform(get("/api/bank/business_acc/{id}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllAccountsForLoggedInUser() throws Exception {
        // Mock the accountService.findAllAccountsForLoggedInUser() method to return a sample ResponseEntity
        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(List.of(new AccountDto()));

        mockMvc.perform(get("/api/bank/user_accounts"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllAccountsForUserById() throws Exception {
        // Mock the accountService.findAllAccountsForUserById(id) method to return a sample ResponseEntity
        when(accountService.findAllAccountsForUserById(1L)).thenReturn(List.of(new AccountDto()));

        mockMvc.perform(get("/api/bank/user_accounts/{id}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testOpenCurrentAccount() throws Exception {
        // Create a sample CurrentAccountCreateDto
        CurrentAccountCreateDto dto = new CurrentAccountCreateDto();

        // Mock the accountService.openCurrentAccount(currentAccountCreateDto) method to return a sample ResponseEntity
        when(accountService.openCurrentAccount(dto)).thenReturn(new CurrentAccountDto());

        mockMvc.perform(post("/api/bank/current_acc/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"sample\": \"data\" }")) // Replace with your actual JSON data
                .andExpect(status().isForbidden());

    }

    @Test
    public void testOpenForeignCurrencyAccount() throws Exception {
        // Create a sample ForeignCurrencyAccountCreateDto
        ForeignCurrencyAccountCreateDto dto = new ForeignCurrencyAccountCreateDto();

        // Mock the accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto) method to return a sample ResponseEntity
        when(accountService.openForeignCurrencyAccount(dto)).thenReturn(new ForeignCurrencyAccountDto());

        mockMvc.perform(post("/api/bank/foreign_currency_acc/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"sample\": \"data\" }")) // Replace with your actual JSON data
                .andExpect(status().isForbidden());

    }

    @Test
    public void testOpenBusinessAccount() throws Exception {
        // Create a sample BusinessAccountCreateDto
        BusinessAccountCreateDto dto = new BusinessAccountCreateDto();

        // Mock the accountService.openBusinessAccount(businessAccountCreateDto) method to return a sample ResponseEntity
        when(accountService.openBusinessAccount(dto)).thenReturn(new BusinessAccountDto());

        mockMvc.perform(post("/api/bank/business_acc/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"sample\": \"data\" }")) // Replace with your actual JSON data
                .andExpect(status().isForbidden());

    }

    @Test
    public void testUpdateAccountName() throws Exception {
        // Mock the accountService.updateAccountName(accountType, id, name) method to return a sample ResponseEntity
        when(accountService.updateAccountName("current_acc", 1L, "New Name")).thenReturn(new AccountDto());

        mockMvc.perform(put("/api/bank/{accountType}/update_name/{id}/{name}", "current_acc", 1L, "New Name"))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testUpdateAccountStatus() throws Exception {
        // Mock the accountService.updateAccountStatus(accountType, id) method to return a sample ResponseEntity
        when(accountService.updateAccountStatus("current_acc", 1L)).thenReturn(new AccountDto());

        mockMvc.perform(put("/api/bank/{accountType}/update_status/{id}", "current_acc", 1L))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testGetAllCompanies() throws Exception {
        // Mock the accountService.findAllCompanies() method to return a sample ResponseEntity
        when(accountService.findAllCompanies()).thenReturn(List.of(new CompanyDto()));

        mockMvc.perform(get("/api/bank/companies"))
                .andExpect(status().isUnauthorized());

    }

}
