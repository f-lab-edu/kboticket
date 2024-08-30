package com.kboticket.domain;

import com.kboticket.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_nm")
    private String orderNm;

    private Long amount;

    private String currency;

    private String method;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "balance_amount")
    private Long balanceAmount;

    @Column(name = "supplied_amount")
    private Long suppliedAmount;

    private Long vat;

    private PaymentStatus status;

    @Column(name = "requested_date")
    private LocalDateTime requestedAt;

    @Column(name = "approved_date")
    private LocalDateTime approvedAt;

    private String type;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "fail_reason")
    private String failReason;





}
