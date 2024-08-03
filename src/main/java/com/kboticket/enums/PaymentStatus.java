package com.kboticket.enums;

public enum PaymentStatus {
    REQUEST("결제요청"),      // 요청 중
    COMPLETED("결제완료"),    // 완료
    FAILED("결제실패"),       // 실패
    CANCELLED_PART("부분취소"),    // 부분취소
    CANCELLED_ALL("전체취소"),    // 전체취소
    REFUNDED("결제환불");     // 환불

    public final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
}
