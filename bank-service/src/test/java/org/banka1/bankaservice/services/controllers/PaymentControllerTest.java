package org.banka1.bankaservice.services.controllers;

import org.banka1.bankservice.controllers.CurrencyExchangeController;
import org.banka1.bankservice.controllers.PaymentController;
import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PaymentController.class)
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void initMocks() {
        // Mock the behavior of the paymentService methods
        Long id = 1L;

        // Mock getPaymentById
        when(paymentService.findPaymentById(id)).thenReturn(new PaymentDto());

        // Mock getAllPaymentsForLoggedInUser
        when(paymentService.findAllPaymentsForLoggedInUser()).thenReturn(List.of(new PaymentDto()));

        // Mock getAllPaymentsForAccount
        String accountNumber = "123456";
        when(paymentService.findAllPaymentsForAccount(accountNumber)).thenReturn(List.of(new PaymentDto()));

        // Mock makePayment
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto();
        when(paymentService.makePayment(any(PaymentCreateDto.class))).thenReturn(new PaymentDto());

        // Mock transferMoney
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        when(paymentService.transferMoney(any(MoneyTransferDto.class))).thenReturn(new PaymentDto());

        // Mock getPaymentReceiverById
        when(paymentService.findPaymentReceiverById(id)).thenReturn(new PaymentReceiverDto());

        // Mock getAllPaymentReceiversForLoggedInUser
        when(paymentService.findAllPaymentReceiversForLoggedInUser()).thenReturn(List.of(new PaymentReceiverDto()));

        // Mock createPaymentReceiver
        PaymentReceiverCreateDto paymentReceiverCreateDto = new PaymentReceiverCreateDto();
        when(paymentService.createPaymentReceiver(any(PaymentReceiverCreateDto.class))).thenReturn(new PaymentReceiverDto());

        // Mock updatePaymentReceiver
        PaymentReceiverUpdateDto paymentReceiverUpdateDto = new PaymentReceiverUpdateDto();
        when(paymentService.updatePaymentReceiver(any(PaymentReceiverUpdateDto.class), any(Long.class))).thenReturn(new PaymentReceiverDto());

        // Mock deletePaymentReceiver
        when(paymentService.deletePaymentReceiver(id)).thenReturn("");
    }

    @Test
    public void testGetPaymentById() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/api/bank/payment/{id}", id))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetAllPaymentsForLoggedInUser() throws Exception {
        mockMvc.perform(get("/api/bank/user_payments"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

//    @Test
//    public void testGetAllPaymentsForAccount() throws Exception {
//        String accountNumber = "123456";
//
//        mockMvc.perform(get("/api/bank/account_payments")
//                        .param("accountNumber", accountNumber))
//                .andExpect(status().isUnauthorized())
//                .andReturn();
//    }

    @Test
    public void testMakePayment() throws Exception {
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto();

        mockMvc.perform(post("/api/bank/make_payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testTransferMoney() throws Exception {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();

        mockMvc.perform(post("/api/bank/transfer_money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetPaymentReceiverById() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/api/bank/payment_receiver/{id}", id))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetAllPaymentReceiversForLoggedInUser() throws Exception {
        mockMvc.perform(get("/api/bank/user_receivers"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testCreatePaymentReceiver() throws Exception {
        PaymentReceiverCreateDto paymentReceiverCreateDto = new PaymentReceiverCreateDto();

        mockMvc.perform(post("/api/bank/create_receiver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testUpdatePaymentReceiver() throws Exception {
        Long id = 1L;
        PaymentReceiverUpdateDto paymentReceiverUpdateDto = new PaymentReceiverUpdateDto();

        mockMvc.perform(put("/api/bank/update_receiver/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testDeletePaymentReceiver() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/bank/delete_receiver/{id}", id))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
