package com.kboticket.service.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private String email;
    private String phone;
}
