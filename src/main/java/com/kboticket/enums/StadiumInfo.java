package com.kboticket.enums;

import lombok.Getter;

@Getter
public enum StadiumInfo {
    DJ("DJ", "대전 한화생명 이글스파크", "대전광역시 중구 대종로 373"),
    KJ("KJ", "기아 챔피언스 필드", "광주광역시 북구 서림로 10"),
    CH("CH", "창원 NC 파크", "경상남도 창원시 마산회원구 삼호로 63 (양덕동)"),
    GO("GO", "고척 스카이돔", "서울특별시 구로구 경인로 430"),
    IN("IN", "인천 SSG 랜더스필드", "인천광역시 미추홀구 문학동 인천문학경기장"),
    DG("DG", "대구 삼성 라이온즈 파크", "대구광역시 수성구 야구전설로 1 대구삼성라이온즈파크"),
    SE("SE", "서울종합운동장 야구장", "서울 송파구 올림픽로 25 서울종합운동장 잠실야구장"),
    SU("SU", "수원 케이티 위즈 파크", "경기도 수원시 장안구 경수대로 893"),
    BU("BU", "부산 사직 야구장", "부산광역시 동래구 사직로 45");

    public final String code;
    public final String name;
    public final String address;

    StadiumInfo(String code, String name, String address) {
        this.code = code;
        this.name = name;
        this.address = address;
    }
}
