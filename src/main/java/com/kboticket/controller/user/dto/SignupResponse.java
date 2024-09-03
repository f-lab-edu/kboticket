package com.kboticket.controller.user.dto;

import com.kboticket.domain.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class SignupResponse {

    private String email;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .email(user.getEmail())
                .build();
    }

}
