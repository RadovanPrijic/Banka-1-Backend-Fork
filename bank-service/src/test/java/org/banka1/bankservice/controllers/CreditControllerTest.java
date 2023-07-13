package org.banka1.bankservice.controllers;

import org.banka1.bankservice.domains.dtos.credit.CreditDto;
import org.banka1.bankservice.domains.dtos.credit.CreditInstallmentDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestDto;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CreditController.class)
@AutoConfigureMockMvc
public class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditService creditService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCreditRequest() throws Exception {
        // Create a sample CreditRequestCreateDto
        CreditRequestCreateDto dto = new CreditRequestCreateDto();

        // Mock the creditService.createCreditRequest(creditRequestCreateDto) method to return a sample ResponseEntity
        when(creditService.createCreditRequest(dto)).thenReturn(new CreditRequestDto());

        mockMvc.perform(post("/api/bank/create_credit_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"sample\": \"data\" }")) // Replace with your actual JSON data
                .andExpect(status().isForbidden());
    }

    @Test
    public void testApproveCreditRequest() throws Exception {
        // Mock the creditService.approveCreditRequest(requestId) method to return a sample ResponseEntity
        when(creditService.approveCreditRequest(1L)).thenReturn(new CreditDto());

        mockMvc.perform(get("/api/bank/approve_credit_request/{requestId}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testDenyCreditRequest() throws Exception {
        // Mock the creditService.denyCreditRequest(requestId) method to return a sample ResponseEntity
        when(creditService.denyCreditRequest(1L)).thenReturn(new CreditRequestDto());

        mockMvc.perform(get("/api/bank/deny_credit_request/{requestId}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllWaitingCreditRequests() throws Exception {
        // Mock the creditService.findAllWaitingCreditRequests() method to return a sample ResponseEntity
        when(creditService.findAllWaitingCreditRequests()).thenReturn(List.of(new CreditRequestDto()));

        mockMvc.perform(get("/api/bank/waiting_credit_requests"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testPayCreditInstallment() throws Exception {
        // Mock the creditService.payCreditInstallment(creditId) method to return a sample ResponseEntity
        when(creditService.payCreditInstallment(1L)).thenReturn(new CreditInstallmentDto());

        mockMvc.perform(get("/api/bank/pay_credit_installment/{creditId}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetCreditRequestById() throws Exception {
        // Mock the creditService.findCreditRequestById(id) method to return a sample ResponseEntity
        when(creditService.findCreditRequestById(1L)).thenReturn(new CreditRequestDto());

        mockMvc.perform(get("/api/bank/credit_request/{id}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllCreditRequestsForLoggedInUser() throws Exception {
        // Mock the creditService.findAllCreditRequestsForLoggedInUser() method to return a sample ResponseEntity
        when(creditService.findAllCreditRequestsForLoggedInUser()).thenReturn(List.of(new CreditRequestDto()));

        mockMvc.perform(get("/api/bank/user_credit_requests"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllCreditRequestsForAccount() throws Exception {
        // Mock the creditService.findAllCreditRequestsForAccount(accountNumber) method to return a sample ResponseEntity
        when(creditService.findAllCreditRequestsForAccount("1234567890")).thenReturn(List.of(new CreditRequestDto()));

        mockMvc.perform(get("/api/bank/account_credit_requests")
                        .param("accountNumber", "1234567890"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetCreditById() throws Exception {
        // Mock the creditService.findCreditById(id) method to return a sample ResponseEntity
        when(creditService.findCreditById(1L)).thenReturn(new CreditDto());

        mockMvc.perform(get("/api/bank/credit/{id}", 1L))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllCreditsForLoggedInUser() throws Exception {
        // Mock the creditService.findAllCreditsForLoggedInUser() method to return a sample ResponseEntity
        when(creditService.findAllCreditsForLoggedInUser()).thenReturn(List.of(new CreditDto()));

        mockMvc.perform(get("/api/bank/user_credits"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllCreditsForAccount() throws Exception {
        // Mock the creditService.findAllCreditsForAccount(accountNumber) method to return a sample ResponseEntity
        when(creditService.findAllCreditsForAccount("1234567890")).thenReturn(List.of(new CreditDto()));

        mockMvc.perform(get("/api/bank/account_credits")
                        .param("accountNumber", "1234567890"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetAllCreditInstallmentsForCredit() throws Exception {
        // Mock the creditService.findAllCreditInstallmentsForCredit(creditId) method to return a sample ResponseEntity
        when(creditService.findAllCreditInstallmentsForCredit(1L)).thenReturn(List.of(new CreditInstallmentDto()));

        mockMvc.perform(get("/api/bank/credit_installments/{creditId}", 1L))
                .andExpect(status().isUnauthorized());

    }

}
