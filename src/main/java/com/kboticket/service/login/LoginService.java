package com.kboticket.service.login;

import com.kboticket.common.constants.KboConstant;
import com.kboticket.common.util.PasswordUtils;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.User;
import com.kboticket.service.login.dto.LoginDto;
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

        User user = userService.getUserByEmail(email);

        if (PasswordUtils.matches(password, user.getPassword())) {
            throw new KboTicketException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessKey = KboConstant.ACCESS_LOCK + KboConstant.BASIC_DLIIMITER + email;
        String refreshKey = KboConstant.REFRESH_LOCK + KboConstant.BASIC_DLIIMITER + email;

        invalidatePreviousToken(accessKey, refreshKey);

        String accessToken = jwtTokenProvider.generateToken(email, TokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generateToken(email, TokenType.REFRESH);

        log.info(accessToken);
        log.info(refreshToken);

        saveToken(accessKey, accessToken, 6 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
        saveToken(refreshKey, refreshToken, 7, TimeUnit.DAYS);

    }

    private void saveToken(String key, String token, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, token, timeout, timeUnit);
    }

    private void invalidatePreviousToken(String accessKey, String refreshKey) {
        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);
    }
}
