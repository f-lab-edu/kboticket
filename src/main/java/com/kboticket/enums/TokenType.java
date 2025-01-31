package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS("access", 3 * 60 * 1000L),
    REFRESH("refresh", 7 * 24 * 60 * 60 * 1000L);

    private final String name;
    private final Long expireTime;

    TokenType(String name, Long expireTime) {
        this.name = name;
        this.expireTime = expireTime;
    }
}
