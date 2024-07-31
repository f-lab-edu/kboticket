package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum TicketStatus {
    ISSUED("발급"),
    USED("사용"),
    CANCELED("취소");

    public final String value;

    TicketStatus(String value) {
        this.value = value;
    }
}