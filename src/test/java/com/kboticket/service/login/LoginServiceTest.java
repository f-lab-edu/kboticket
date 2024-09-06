package com.kboticket.service.login;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.dto.login.LoginDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TokenType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class LoginServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        // given
        String email = "test@naver.com";
        String password = "1111";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        // when
        given(userService.isExistEmail(email)).willReturn(true);
        given(userService.checkPassword(email, password)).willReturn(true);
        given(jwtTokenProvider.generateToken(email, TokenType.ACCESS)).willReturn(accessToken);
        given(jwtTokenProvider.generateToken(email, TokenType.REFRESH)).willReturn(refreshToken);

        // then
        loginService.login(LoginDto.builder().build());

        // then
        verify(userService).isExistEmail(email);
        verify(userService).checkPassword(email, password);
        verify(jwtTokenProvider).generateToken(email, TokenType.ACCESS);
        verify(jwtTokenProvider).generateToken(email, TokenType.REFRESH);

        // Redis에 토큰이 잘 저장되었는지 확인
        verify(redisTemplate).opsForValue().set(eq("access:" + email), eq(accessToken), eq(6 * 60 * 60 * 1000L), eq(TimeUnit.MILLISECONDS));
        verify(redisTemplate).opsForValue().set(eq("refresh:" + email), eq(refreshToken), eq(7L), eq(TimeUnit.DAYS));

    }

    @Test
    void login_EmailDoseNotExists() {
        // given
        String email = "test@example.com";
        String password = "password";

        LoginDto loginDto = LoginDto.builder()
                .username(email)
                .password(password)
                .build();

        doThrow(new KboTicketException(ErrorCode.NOT_FOUND_USER)).when(userService).isExistEmail(email);

        // when / then
        assertThrows(KboTicketException.class, () -> loginService.login(loginDto));

        verify(userService).isExistEmail(email);
    }

    @Test
    void login_IncorrectPassword() {
        // given
        String email = "test@example.com";
        String password = "password";

        LoginDto loginDto = LoginDto.builder()
                .username("test@naver.com")
                .password("1111").build();

        // 비밀번호가 틀린 경우 Mock
        given(userService.checkPassword(email, password)).willReturn(false);

        // when
        KboTicketException kboTicketException = assertThrows(KboTicketException.class,
                () -> loginService.login(loginDto));

        // then
        assertEquals(kboTicketException.getCode(), ErrorCode.INCORRECT_PASSWORD.code);
    }

    @Test
    void login_GenerateTokenTest() {
        // given
        String email = "test@example.com";
        String password = "password";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        LoginDto loginDto = LoginDto.builder()
                .username(email)
                .password(password)
                .build();

        // 토큰 생성 및 비밀번호 확인 Mock
        given(userService.isExistEmail(email)).willReturn(true);
        given(userService.checkPassword(email, password)).willReturn(true);
        given(jwtTokenProvider.generateToken(email, TokenType.ACCESS)).willReturn(accessToken);
        given(jwtTokenProvider.generateToken(email, TokenType.REFRESH)).willReturn(refreshToken);

        // RedisTemplate 동작 시 예외 발생 Mock
        doThrow(new RuntimeException("Redis error")).when(redisTemplate).opsForValue().set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // when / then
        assertThrows(RuntimeException.class, () -> loginService.login(loginDto));

        verify(redisTemplate).opsForValue().set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}