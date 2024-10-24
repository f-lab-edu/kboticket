package com.kboticket.controller.payment;

import com.kboticket.common.CommonResponse;
import com.kboticket.dto.payment.PaymentFailResponse;
import com.kboticket.dto.payment.PaymentRequest;
import com.kboticket.dto.payment.PaymentSuccessResponse;
import com.kboticket.service.payment.PaymentService;
import com.kboticket.service.ticket.TicketService;
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
  private final TicketService ticketService;

  /**
   * 결제  요청
   */
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public void requestPayment(Authentication authentication, @RequestBody @Valid PaymentRequest paymentRequest) {
    String email = authentication.getName();
    Long gameId = paymentRequest.getGameId();
    Long amount = paymentRequest.getAmount();

    paymentService.createOrderAndRequestPayment(email, gameId, amount);

  }

  /**
   * 결제 성공
   */
  @GetMapping("/success")
  public CommonResponse<PaymentSuccessResponse> success(Authentication authentication,
      @RequestParam String paymentKey,
      @RequestParam String orderId,
      @RequestParam Long amount) {
    String email = authentication.getName();
    PaymentSuccessResponse paymentSuccessResponse = paymentService.paymentSuccess(paymentKey, orderId, amount);

    // 티켓 생성
    if (paymentSuccessResponse != null) {
      ticketService.createTicket(orderId);
    }

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

}
