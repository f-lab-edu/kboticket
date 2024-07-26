package com.kboticket.controller;

import com.kboticket.common.CommonResponse;
import com.kboticket.domain.Payment;
import com.kboticket.dto.payment.PaymentDto;
import com.kboticket.dto.payment.PaymentRequesteDto;
import com.kboticket.dto.payment.PaymentSuccessDto;
import com.kboticket.service.PaymentService;
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

    private final PaymentService paymentService;

    @PostMapping
    public CommonResponse<Payment> requestPayment(@RequestBody @Valid PaymentRequesteDto paymentRequesteDto,
                                                    Authentication authentication) {
        String email = authentication.getName();

        Payment payment = paymentService.requestPayment(paymentRequesteDto, email);

        return new CommonResponse<>(payment);
    }

    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<PaymentSuccessDto> success(@RequestParam String paymentKey,
                                                     @RequestParam String orderId,
                                                     @RequestParam Long amount) {
        PaymentSuccessDto paymentSuccessDto = paymentService.paymentSuccess(paymentKey, orderId, amount);
        return new CommonResponse<>(paymentSuccessDto);
    }

    @GetMapping("/fail")
    public void fail() {
        log.info("fail");
    }

    @GetMapping("/view")
    public CommonResponse<PaymentDto> paymentOrderDetail(@RequestParam String orderId) {
        PaymentDto paymentDto = paymentService.getOrderDetail(orderId);

        return new CommonResponse<>(paymentDto);
    }
}
