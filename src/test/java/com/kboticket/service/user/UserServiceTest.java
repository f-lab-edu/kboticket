package com.kboticket.service.user;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.domain.User;
import com.kboticket.dto.user.UserPasswordDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.UserRepository;
import com.kboticket.service.user.UserService;
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
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("[SUCCESS] 회원가입 성공")
    void signSuccessTest() {
        // given

        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@test.com")
                .password("1111")
                .confirmpassword("1111")
                .verificationKey("11111")
                .terms(new ArrayList<>())
                .build();

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(jwtTokenProvider.getPhoneFromToken(signupRequest.getVerificationKey())).willReturn("01012345678");
        given(bCryptPasswordEncoder.encode(signupRequest.getPassword())).willReturn("1111");

        // when
        userService.signup(signupRequest);

        // then
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("[SUCCESS] 이메일 중복 검사")
    void signupDuplicateEmail() {
        // given
        String email = "test@naver.com";
        given(userRepository.existsByEmail(email)).willReturn(true);
        // when
        KboTicketException exception = assertThrows(KboTicketException.class, () -> {
            userService.signup(new SignupRequest(email, "password", "password", "key", null));
        });

        // then
        assertEquals(ErrorCode.EMAIL_DUPLICATTE.message, exception.getMessage());
    }

    @Test
    @DisplayName("[SUCCESS] 비밀번호 불일치")
    void passwordMismatchTest() {
        // given
        String email = "test@naver.com";
        String inputPassword  = "1111";

        User user = User.builder()
                .email(email)
                .password(inputPassword)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.matches(inputPassword, user.getPassword())).willReturn(true);

        // when
        boolean result = userService.checkPassword(email, inputPassword);

        // then
        assertTrue(result);
    }


    @Test
    @DisplayName("[SUCCESS] 비밀번호 변경")
    void passwordUpdateTest() {
        // given
        UserPasswordDto userPasswordDto = new UserPasswordDto();
        userPasswordDto.setEmail("test@test.com");
        userPasswordDto.setCurrentPassword("1111");
        userPasswordDto.setNewPassword("0000");
        userPasswordDto.setConfirmPassword("0000");

        User user = User.builder()
                .email("test@naver.com")
                .password("1111")
                .build();

        when(userRepository.findByEmail(userPasswordDto.getEmail())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(userPasswordDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(bCryptPasswordEncoder.encode(userPasswordDto.getNewPassword())).thenReturn("encodedNewPassword");

        // when
        userService.updatePassword(userPasswordDto);

        // then
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());

    }

    @Test
    @DisplayName("[SUCCESS] 기존 비밀번호 확인")
    void checkPasswordTest() {
        // given
        String email = "test@test.com";
        String inputPassword = "1111";

        User user = User.builder()
                .email("test@naver.com")
                .password("1111")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(inputPassword, user.getPassword())).thenReturn(true);

        // when
        boolean result = userService.checkPassword(email, inputPassword);

        // then
        assertTrue(result);
    }
}
