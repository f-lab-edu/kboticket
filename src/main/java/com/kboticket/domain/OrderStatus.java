package com.kboticket.domain;

public enum OrderStatus {
    ORDER("주문중"),
    COMPLETE("주문완료"),
    CANCEL("주문취소");

    public final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
