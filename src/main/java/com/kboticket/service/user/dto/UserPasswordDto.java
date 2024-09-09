package com.kboticket.service.user.dto;

import com.kboticket.controller.user.dto.ChangePasswordRequest;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
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
