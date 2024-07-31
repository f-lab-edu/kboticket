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
    public CommonResponse<PaymentSuccessResponse> success(@RequestParam String paymentKey,
                                                          @RequestParam String orderId,
                                                          @RequestParam Long amount) {
        PaymentSuccessResponse paymentSuccessResponse = paymentService.paymentSuccess(paymentKey, orderId, amount);

        return new CommonResponse<>(paymentSuccessResponse);
    }

    /**
     * 결제 실패
     */
    @GetMapping("/fail")
    public CommonResponse<PaymentFailResponse> fail(@RequestParam String code,
                                                    @RequestParam String orderId,
                                                    @RequestParam String message) {
        PaymentFailResponse paymentFailResponse = paymentService.paymentFail(code, orderId, message);

        return new CommonResponse<>(paymentFailResponse);
    }

    /**
     * 결제 취소
     */
    @GetMapping("/cancel")
    public CommonResponse<PaymentCancelResponse> cancel(@RequestParam String paymentKey,
                                                        @RequestParam String cancelReason) {
        PaymentCancelResponse paymentCancelResponse = paymentService.paymentCancel(paymentKey, cancelReason);

        return new CommonResponse<>(paymentCancelResponse);
    }

    /**
     * 결제 내역
     */
    @GetMapping("/history")
    public CommonResponse<List<PaymentHistoryResponse>> paymentOrderDetail(Authentication authentication) {
        String email = authentication.getName();

        List<PaymentHistoryResponse> paymentHistory = paymentService.getPaymentHistory(email);

        return new CommonResponse<>(paymentHistory);
    }
}
