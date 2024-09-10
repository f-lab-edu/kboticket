package com.kboticket.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.CommonResponse;
import com.kboticket.controller.order.dto.OrderDetailResponse;
import com.kboticket.controller.order.dto.OrderListRequest;
import com.kboticket.controller.order.dto.OrderListResponse;
import com.kboticket.domain.OrderStatus;
import com.kboticket.dto.order.OrderSearchDto;
import com.kboticket.service.order.OrderService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ExtendWith(SpringExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        OrderController orderController = new OrderController(orderService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 유저 주문 내역 조회 ")
    void listTest() throws Exception {
        // given/ when
        String email =  authentication.getName();
        given(email).willReturn("test@example.com");

        OrderListRequest request = OrderListRequest.builder()
                .orderStatus(OrderStatus.COMPLETE)
                .periodType("3")
                .dataType("orderDate")
                .build();

        OrderListResponse response = new OrderListResponse(Collections.emptyList());

        given(orderService.getOrderList(any(OrderSearchDto.class), anyString(), anyInt()))
                .willReturn(response);
        String json = new ObjectMapper().writeValueAsString(request);

        // then
        mockMvc.perform(post("/orders/history")
                .contentType(APPLICATION_JSON)
                .param("cursor", "1")
                .param("limit", "5")
                .content(json)
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(orderService, times(1))
                .getOrderList(any(OrderSearchDto.class), eq("1"), eq(5));
    }

    @Test
    @DisplayName("[SUCCESS] 주문 상세 정보")
    void viewTest() throws Exception {
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

        given(orderService.getOrderDetails("12345")).willReturn(orderDetailResponse);

        String responseJson = new ObjectMapper().writeValueAsString(new CommonResponse<>(orderDetailResponse));

        mockMvc.perform(get("/orders/12345")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(responseJson));

        verify(orderService, times(1)).getOrderDetails("12345");
    }
}