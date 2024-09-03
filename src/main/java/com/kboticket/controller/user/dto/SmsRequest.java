package com.kboticket.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SmsRequest {
    private String phone;
    private String certificationNumber;
}
