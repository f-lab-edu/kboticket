package com.kboticket.controller;

import com.kboticket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 결제 완료 후 주문 생성
     */
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody Long userId, @RequestBody String reservationId) {
        // 원래는 결제할때  레디스걸음. 재고를 차감하기 위해서, 결제는 한 명만 성공
        // 따로 말고 같이 처리  8분 처리했을때 바로 결제까지 해야함
        // msa , 서비스 확장이 안됨, ex 넷ㅅ플릭스, -> 기능별로 쪼갬, 스프링끼리 서로 필요할 때 api 호출, 스프링 실시간으로 늘음,
        // 주문과 결제가 나눠져있을 수 있음.
        // 재결제, 만료시간, 결제하다가 실패하면 좌석 select 부터, 결제하는 쪽에 아ㅏㅁ호화해서 order, 키 -> 유효한 기간동안 결제가능하도록, 실패하면 키를 넘겨주고 다시 요청
        orderService.order(userId, reservationId);
    }
}
