package com.kboticket.controller.user.dto;

import com.kboticket.dto.user.UserPasswordDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    /* 변경 전 password */
    private String currentPassword;
    /* 변경 할 password */
    private String newPassword;
    /* 변경 할 password 확인 */
    private String confirmPassword;
}
