package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.OrderResponse;
import com.kboticket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 유저의 주문 내역
     */
    @GetMapping("/history")
    public CommonResponse<OrderResponse> list(Authentication authentication) {
        // 페이징 처리 필요
        // 기간으로 검색 필
        String email = authentication.getName();
        OrderResponse orderResponse = orderService.getOrderList(email);

        return new CommonResponse<>(orderResponse);
    }

    /**
     * 주문 상세 정보
     */
    @PostMapping("/{orderId}")
    public void orderView(@PathVariable Long orderId) {
        // 예매정보
        /*
                티켓명	[2024 신한 SOL Bank KBO 리그]
                삼성 라이온즈 vs 한화이글스	예매자	변유림
                관람일시	2024.06.01(토) 17:00	장소	대구 삼성라이온즈파크
                좌석
                1루 내야지정석 1-9구역 5열 3번

                1루 내야지정석 1-9구역 5열 4번

                티켓수령 방법	현장수령
                예매일	2024.05.24	현재상태	예매완료
                결제수단
                신용카드 간편결제
                예매채널	PC웹
         */

    }

}
