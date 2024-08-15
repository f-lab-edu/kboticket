package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.payment.PaymentFailResponse;
import com.kboticket.dto.payment.PaymentRequestDto;
import com.kboticket.dto.payment.PaymentSuccessResponse;
import com.kboticket.service.OrderFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderFacade orderFacade;

    /**
     * 결제  요청
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void requestPayment(@RequestBody @Valid PaymentRequestDto paymentRequestDto,
                               Authentication authentication) {
        String loginId = authentication.getName();

        orderFacade.createOrderAndRequestPayment(paymentRequestDto, loginId);
    }

    /**
     * 결제 성공
     */
    @GetMapping("/success")
    public CommonResponse<PaymentSuccessResponse> success(@RequestParam String paymentKey,
                                                          @RequestParam String orderId,
                                                          @RequestParam Long amount) {
        PaymentSuccessResponse paymentSuccessResponse = orderFacade.handlePaymentSuccess(paymentKey, orderId, amount);

        return new CommonResponse<>(paymentSuccessResponse);
    }

    /**
     * 결제 실패
     */
    @GetMapping("/fail")
    public CommonResponse<PaymentFailResponse> fail(@RequestParam String code,
                                                    @RequestParam String orderId,
                                                    @RequestParam String message) {
        PaymentFailResponse paymentFailResponse = orderFacade.handlePaymentFailure(code, orderId, message);

        return new CommonResponse<>(paymentFailResponse);
    }

}
