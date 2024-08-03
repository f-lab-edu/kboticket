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
        // 페이징 처리 필요
        // 기간으로 검색 필요
        String email = authentication.getName();
        OrderResponse orderResponse = orderFacade.getOrderList(email);

        return new CommonResponse<>(orderResponse);
    }

    /**
     * 주문 상세 정보
     */
    @PostMapping("/{orderId}")
    public void orderView(@PathVariable Long orderId) {
        // 예매정보
        /*
            티켓명
            예매자
            관람일시
            장소
            좌석 - 1루 내야지정석 1-9구역 5열 3번/ 1루 내야지정석 1-9구역 5열 4번
            예매일
            현재상태 - 예매완료

            결제수단 - 신용카드 간편결제
            예매채널	PC웹
         */

    }

}
