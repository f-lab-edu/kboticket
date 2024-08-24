package com.kboticket.controller.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateUserRequest {
    private String city;
    private String street;
    private String zipcode;
}
