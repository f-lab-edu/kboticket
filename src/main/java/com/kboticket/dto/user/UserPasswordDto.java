package com.kboticket.dto.user;

import com.kboticket.controller.user.dto.ChangePasswordRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserPasswordDto {
    
    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public static UserPasswordDto of(String email, ChangePasswordRequest request) {
        return UserPasswordDto
                .builder()
                .email(email)
                .currentPassword(request.getCurrentPassword())
                .newPassword(request.getNewPassword())
                .confirmPassword(request.getConfirmPassword())
                .build();
    }
}
