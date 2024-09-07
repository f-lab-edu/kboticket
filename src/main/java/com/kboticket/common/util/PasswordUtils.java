package com.kboticket.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String encrypt(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    public static String validate(String rawPassword) {
        // 정규식 추가
        return rawPassword;
    }

    public static Boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
