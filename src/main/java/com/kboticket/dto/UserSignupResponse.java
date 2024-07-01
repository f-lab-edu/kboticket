package com.kboticket.dto;

import com.kboticket.domain.User;
import lombok.*;

import java.lang.reflect.Member;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserSignupResponse {

    private String email;

    public static UserSignupResponse from(User user) {
        return UserSignupResponse.builder()
                .email(user.getEmail())
                .build();
    }

}
