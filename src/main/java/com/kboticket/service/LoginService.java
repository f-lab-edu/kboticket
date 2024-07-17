package com.kboticket.service;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.User;
import com.kboticket.dto.LoginRequestDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.enums.TokenType;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void login(LoginRequestDto loginRequestDto) {
        // 이메일로 존재하는 유저인지 확인
        existsByEmail(loginRequestDto);
        // 비밀번호 일치 확인
        checkPassword(loginRequestDto);

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(loginRequestDto.getUsername(), TokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generateToken(loginRequestDto.getUsername(), TokenType.REFRESH);

        log.info(accessToken);
        log.info(refreshToken);

        saveToken("access:" + loginRequestDto.getUsername(), accessToken, 6 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
        saveToken("refresh:" + loginRequestDto.getUsername(), refreshToken, 7, TimeUnit.DAYS);

    }

    private void saveToken(String key, String token, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, token, timeout, timeUnit);
    }

    private void existsByEmail(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getUsername();

        boolean isExist = userRepository.existsByEmail(email);
        if (!isExist) {
            throw new KboTicketException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    private void checkPassword(LoginRequestDto loginRequestDto) {

        String inputPassword = loginRequestDto.getPassword();

        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDto.getUsername());
        User user = optionalUser.get();
        String storedPassword = user.getPassword();

        if (!bCryptPasswordEncoder.matches(inputPassword, storedPassword)) {
            throw new KboTicketException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
}
