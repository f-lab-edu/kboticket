package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.OrderResponse;
import com.kboticket.service.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    /**
     * 유저의 주문 내역
     */
    @GetMapping("/history")
    public CommonResponse<OrderResponse> list(Authentication authentication) {
        String email = authentication.getName();
        OrderResponse orderResponse = orderFacade.getOrderList(email);

        return new CommonResponse<>(orderResponse);
    }

    /**
     * 주문 상세 정보
     */
    @GetMapping("/{orderId}")
    public void orderView(@PathVariable Long orderId) {
        // 예매정보

    }

}
