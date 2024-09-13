package com.kboticket.controller.payment;

import com.kboticket.service.payment.PaymentService;
import com.kboticket.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    @DisplayName("[SUCCESS]")
    void requestPaymentTest() {

    }

    @Test
    @DisplayName("[SUCCESS]")
    void successTest() {

    }

    @Test
    @DisplayName("[SUCCESS]")
    void failTest() {

    }


}