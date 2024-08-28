package com.kboticket.controller.order;

import com.kboticket.common.CommonResponse;
import com.kboticket.controller.order.dto.OrderDetailResponse;
import com.kboticket.controller.order.dto.OrderListRequest;
import com.kboticket.controller.order.dto.OrderListResponse;
import com.kboticket.dto.order.OrderSearchDto;
import com.kboticket.service.order.OrderService;
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
    @PostMapping("/history")
    public CommonResponse<OrderListResponse> list(Authentication authentication,
                                                  @RequestBody OrderListRequest orderListRequest,
                                                  @RequestParam(value = "cursor", required = false) String cursor,
                                                  @RequestParam(value = "limit", defaultValue = "5") int limit) {
        String email = authentication.getName();
        OrderSearchDto orderSearchDto = OrderSearchDto.of(email, orderListRequest);
        OrderListResponse response = orderService.getOrderList(orderSearchDto, cursor, limit);

        return new CommonResponse<>(response);
    }

    /**
     * 주문 상세 정보
     */
    @GetMapping("/{orderId}")
    public CommonResponse<OrderDetailResponse> orderView(@PathVariable String orderId) {
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetails(orderId);

        return new CommonResponse<>(orderDetailResponse);
    }
}
