package com.kboticket.service;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.User;
import com.kboticket.dto.LoginRequestDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.EmailNotFoundException;
import com.kboticket.exception.PasswordMismatchException;
import com.kboticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public String login(LoginRequestDto loginRequestDto) {
        // 이메일로 존재하는 유저인지 확인
        existsByEmail(loginRequestDto);

        // 비밀번호 일치 확인
        checkPassword(loginRequestDto);

        String token = jwtTokenProvider.generateToken(loginRequestDto.getUsername());

        redisTemplate.opsForValue().set("auth", token);

        return token;
    }

    private void existsByEmail(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getUsername();

        boolean isExist = userRepository.existsByEmail(email);
        if (!isExist) {
            throw new EmailNotFoundException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    private void checkPassword(LoginRequestDto loginRequestDto) {

        String inputPassword = loginRequestDto.getPassword();

        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDto.getUsername());
        User user = optionalUser.get();
        String storedPassword = user.getPassword();

        if (!bCryptPasswordEncoder.matches(inputPassword, storedPassword)) {
            throw new PasswordMismatchException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

}
