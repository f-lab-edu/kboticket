package com.kboticket.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private String email;
    private String phone;
}
