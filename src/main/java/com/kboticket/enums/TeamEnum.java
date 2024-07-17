package com.kboticket.enums;

public enum TeamEnum {

    HH("HH", "한화 이글스", "DJ"),
    HT("HT", "KIA 타이거즈", "KJ"),
    KT("KT", "KT wiz", "SU"),
    LG("LG", "LG 트윈스", "SE"),
    LT("LT", "롯데 자이언츠", "BU"),
    NC("NC", "NC 다이노스", "CH"),
    OB("OB", "두산 베어스", "SE"),
    SK("SK", "SSG 랜더스", "IN"),
    SS("SS", "삼성 라이온즈", "DG"),
    WO("WO", "키움 히어로즈", "GO");

    public final String code;
    public final String name;
    public final String stadiumCode;

    TeamEnum(String code, String name, String stadiumCode) {
        this.code = code;
        this.name = name;
        this.stadiumCode = stadiumCode;
    }

}
