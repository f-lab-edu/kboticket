package com.kboticket.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * JWT 토큰에 관련된 암호화, 복호화, 검증로직
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateToken(String email){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + jwtProperties.getExpired()), email);
    }

    private String makeToken(Date expiry, String email) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // 유효성 검증
    public boolean validToken(String token) {
        try{
            Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException |
                MalformedJwtException | SecurityException e) {
            log.info(e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        // 토큰 복호
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        // UserDetails 객체를 만들어서 Authentication 리턴
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public String getPhoneFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set("black:" + token, "black", jwtProperties.getExpired());
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("black:" + token);
    }
}
