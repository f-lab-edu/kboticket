package com.kboticket.service.login;

import com.kboticket.common.constants.Constant;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader(Constant.HEADER_AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(Constant.TOKEN_PREFIX)) {
            throw new KboTicketException(ErrorCode.INVALID_AUTHORIZATION);
        }

        String token =  jwtTokenProvider.resolveToken(authHeader);
        if (token != null && jwtTokenProvider.validToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);

            String accessKey = String.format("blacklist:%s", token);
            redisTemplate.opsForValue().set(accessKey, "logout", jwtTokenProvider.getExpiration(token), TimeUnit.MILLISECONDS);

            jwtTokenProvider.deleteStoredToken(String.format("refresh:%s", email));
        }
    }
}
