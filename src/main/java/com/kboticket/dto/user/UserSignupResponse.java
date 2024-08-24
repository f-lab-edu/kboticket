package com.kboticket.dto.user;

import com.kboticket.domain.User;
import lombok.*;

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
