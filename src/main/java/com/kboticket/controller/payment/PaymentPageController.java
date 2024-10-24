package com.kboticket.controller.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PaymentPageController {

    @GetMapping("/payment-page")
    public String showPaymentPage(Model model) {
        model.addAttribute("orderId", "12345"); // 예시로 주문 ID 추가
        return "payment";
    }
}