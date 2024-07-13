package com.kboticket.domain;

public enum TicketStatus {
    HOLD("Hold"),
    RESERVED("Reserved"),
    ISSUED("Issued"),
    EXPIRED("Expired"),
    USED("Used");

    private String value;

    TicketStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}