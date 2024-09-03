package com.kboticket.dto;

import com.kboticket.controller.user.dto.SmsRequest;
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
