package com.kboticket.service.user.dto;

import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.dto.TermsDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private String email;
    private String phone;
    private String password;
    private String confirmpassword;
    private String vertificationKey;
    private List<TermsDto> terms;

    public static UserDto from(SignupRequest request) {
        return UserDto.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .confirmpassword(request.getConfirmpassword())
            .terms(request.getTerms())
            .vertificationKey(request.getVerificationKey())
            .build();
    }

}
