package com.kboticket.dto.user;

import com.kboticket.controller.user.dto.UpdateUserRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoDto {

    private String email;
    private String city;
    private String street;
    private String zipcode;

    public static UserInfoDto from(UpdateUserRequest request) {
        return UserInfoDto.builder()
                .city(request.getCity())
                .street(request.getStreet())
                .zipcode(request.getZipcode())
                .build();
    }
}
