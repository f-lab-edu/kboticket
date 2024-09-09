package com.kboticket.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SmsRequestDto {
    private String phone;
    private String certificationNumber;

    public static SmsRequestDto from(SmsRequest request) {
        return SmsRequestDto.builder()
                .phone(request.getPhone())
                .certificationNumber(request.getCertificationNumber())
                .build();
    }
}
