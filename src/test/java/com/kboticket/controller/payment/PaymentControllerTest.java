package com.kboticket.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.dto.payment.PaymentFailResponse;
import com.kboticket.dto.payment.PaymentRequest;
import com.kboticket.dto.payment.PaymentSuccessResponse;
import com.kboticket.service.payment.PaymentService;
import com.kboticket.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@ExtendWith(SpringExtension.class)
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private TicketService ticketService;

    @Mock
    private Authentication authentication;


    @BeforeEach
    void setUp() {
        PaymentController paymentController = new PaymentController(paymentService, ticketService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 결제 요청")
    void requestPaymentTest() throws Exception {

        PaymentRequest request = PaymentRequest.builder()
                .gameId(1L)
                .amount(1000L)
                .build();

        String email = "test@naver.com";
        given(authentication.getName()).willReturn(email);
        doNothing().when(paymentService).createOrderAndRequestPayment(any(), any(), any());

        String json = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[SUCCESS] 결제 성공 ")
    void testPaymentSuccess() throws Exception {
        String paymentKey = "paymentkey";
        String orderId = "1";
        String amout = "1000";
        PaymentSuccessResponse response = new PaymentSuccessResponse();
        given(paymentService.paymentSuccess(any(), any(), any())).willReturn(response);

        doNothing().when(ticketService).createTicket(any());

        mockMvc.perform(get("/payment/success")
                .param("paymentKey", paymentKey)
                .param("orderId", orderId)
                .param("amount", amout))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[SUCCESS] 결제 실패 ")
    void testPaymentFail() throws Exception {
        String code = "1";
        String orderId = "1";
        String message = "message";

        PaymentFailResponse response = new PaymentFailResponse();
        given(paymentService.paymentFail(any(), any(), any())).willReturn(response);

        mockMvc.perform(get("/payment/fail")
                .param("code", code)
                .param("orderId", orderId)
                .param("message", message))
                .andExpect(status().isOk());
    }
}