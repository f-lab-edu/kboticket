package com.kboticket.domain;

public enum OrderStatus {
    ORDER("주문중"),
    COMPLETE("주문완료"),
    CANCELLED_PART("부분취소"),
    CANCELLED_ALL("전체취소");

    public final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
