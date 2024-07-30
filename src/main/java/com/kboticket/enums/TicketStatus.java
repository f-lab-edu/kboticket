package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum TicketStatus {
    HOLD("Hold"),
    RESERVED("Reserved"),
    ISSUED("Issued"),
    EXPIRED("Expired"),
    USED("Used");

    public final String value;

    TicketStatus(String value) {
        this.value = value;
    }
}