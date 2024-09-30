package com.kboticket.controller.user.dto;

import com.kboticket.dto.TermsDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String confirmpassword;

    private String verificationKey;

    private List<TermsDto> terms;

}
