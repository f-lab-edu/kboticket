package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum GameStatus {
    SCHEDULED("예정"),
    OPEN("오픈"),
    EXPIRED("종료");

    public final String status;


    GameStatus(String status) {
        this.status = status;
    }
}
