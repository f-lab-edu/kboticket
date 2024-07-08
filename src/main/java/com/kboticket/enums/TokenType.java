package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS(1 * 60 * 60 * 1000L),
    REFRESH(7 * 24 * 60 * 60 * 1000L);

    private final Long expireTime;

    TokenType(Long expireTime) {
        this.expireTime = expireTime;
    }
}
