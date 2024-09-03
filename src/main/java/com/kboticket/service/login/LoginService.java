package com.kboticket.service.login;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.dto.login.LoginDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TokenType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void login(LoginDto loginDto) {
        String email = loginDto.getUsername();
        String password = loginDto.getPassword();

        // 이메일로 존재하는 유저인지 확인
        userService.isExistEmail(email);

        // 비밀번호 일치 확인
        if (!userService.checkPassword(email, password)) {
            throw new KboTicketException(ErrorCode.INCORRECT_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(email, TokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generateToken(email, TokenType.REFRESH);

        log.info(accessToken);
        log.info(refreshToken);

        saveToken("access:" + email, accessToken, 6 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
        saveToken("refresh:" + email, refreshToken, 7, TimeUnit.DAYS);

    }

    private void saveToken(String key, String token, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, token, timeout, timeUnit);
    }
}
