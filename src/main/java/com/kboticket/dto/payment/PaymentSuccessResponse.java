package com.kboticket.dto.payment;

import lombok.Data;

@Data
public class PaymentSuccessResponse {
    private String mid;
    private String version;
    private String paymentKey;
    private String orderId;
    private String amount;
    private String orderName;
    private String currency;
    private String method;
    private String totalAmount;
    private String balanceAmount;
    private String suppliedAmount;
    private String vat;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private String useEscrow;
    private String cultureExpense;
    private String type;
}
