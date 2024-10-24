package com.kboticket.service.user;

import com.kboticket.common.util.PasswordUtils;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.domain.User;
import com.kboticket.service.user.dto.UserDto;
import com.kboticket.service.user.dto.UserPasswordDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
            .email("test@example.com")
            .password(new BCryptPasswordEncoder().encode("Password12**"))
            .phone("010-1234-5678")
            .build();
    }


    @Test
    @DisplayName("[SUCCESS] 회원가입 성공")
    void signSuccessTest() {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("test@test.com")
            .password("Password12**")
            .confirmpassword("Password12**")
            .verificationKey("111")
            .terms(new ArrayList<>())
            .build();

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(jwtTokenProvider.getPhoneFromToken(request.getVerificationKey()))
            .willReturn("010-1234-5678");

        UserDto userDto = UserDto.from(request);

        // when
        userService.signup(userDto);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("[SUCCESS] 이메일 중복 검사")
    void signupDuplicateEmail() {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("test@test.com")
            .password("Password12**")
            .confirmpassword("Password12**")
            .verificationKey("11111")
            .terms(new ArrayList<>())
            .build();

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        UserDto userDto = UserDto.from(request);

        // when & then
        KboTicketException exception = assertThrows(KboTicketException.class, () -> {
            userService.signup(userDto);
        });

        assertEquals(ErrorCode.EMAIL_DUPLICATTE.code, exception.getCode());
    }

    @Test
    @DisplayName("[SUCCESS] 비밀번호 불일치")
    void verifyPassword() {
        // given
        String email = "test@example.com";
        String correctPassword = "Password12**";

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

        // when
        boolean result = userService.verifyPassword(email, correctPassword);

        // then
        assertTrue(result);
    }


    @Test
    @DisplayName("[SUCCESS] 비밀번호 변경")
    void passwordUpdateTest() {
        // given
        String email = "test@example.com";
        UserPasswordDto userPasswordDto = UserPasswordDto.builder()
            .email(email)
            .currentPassword("Password12**")
            .confirmPassword("Password12**")
            .newPassword("Password123**")
            .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        userService.updatePassword(userPasswordDto);

        // then
        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(PasswordUtils.matches("newPassword", testUser.getPassword()));
    }

    @Test
    @DisplayName("[FAIL] 비밀번호 변경")
    void updatePasswordWithIncorrectCurrentPasswordTest() {
        // given
        String email = "test@example.com";
        UserPasswordDto userPasswordDto = UserPasswordDto.builder()
            .email(email)
            .currentPassword("wrongPassword")
            .confirmPassword("Password12**")
            .newPassword("Password1234**")
            .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

        // when
        KboTicketException exception = assertThrows(KboTicketException.class, () -> {
            userService.updatePassword(userPasswordDto);
        });

        // then
        assertEquals(ErrorCode.INCORRECT_PASSWORD.code, exception.getCode());
    }
}
