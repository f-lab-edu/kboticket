package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.payment.*;
import com.kboticket.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 요청
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void requestPayment(@RequestBody @Valid PaymentRequestDto paymentRequestDto,
                               Authentication authentication) {
        String loginId = authentication.getName();

        paymentService.requestPayment(paymentRequestDto, loginId);
    }

    /**
     * 결제 성공
     */
    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<PaymentSuccessDto> success(@RequestParam String paymentKey,
                                                     @RequestParam String orderId,
                                                     @RequestParam Long amount) {
        PaymentSuccessDto paymentSuccessDto = paymentService.paymentSuccess(paymentKey, orderId, amount);

        return new CommonResponse<>(paymentSuccessDto);
    }

    /**
     * 결제 실패
     */
    @GetMapping("/fail")
    public CommonResponse<PaymentFailDto> fail(@RequestParam String paymentKey,
                                               @RequestParam String orderId) {

        PaymentFailDto paymentFailDto = paymentService.paymentFail(orderId);

        return new CommonResponse<>(paymentFailDto);
    }

    /**
     * 결제 취소
     */
    @GetMapping("/cancel")
    public CommonResponse<PaymentCancelDto> cancel(Authentication authentication,
                                                   @RequestParam String paymentKey,
                                                   @RequestParam String reason) {
        PaymentCancelDto paymentCancelDto = paymentService.paymentCancel(paymentKey, reason);

        return new CommonResponse<>(paymentCancelDto);
    }

    /**
     * 결제 내역
     */
    @GetMapping("/history")
    public CommonResponse<List<PaymentHistoryDto>> paymentOrderDetail(Authentication authentication) {

        String email = authentication.getName();

        List<PaymentHistoryDto> paymentHistory = paymentService.getPaymentHistory(email);

        return new CommonResponse<>(paymentHistory);
    }
}
