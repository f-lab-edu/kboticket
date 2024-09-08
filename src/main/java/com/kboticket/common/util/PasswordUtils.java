package com.kboticket.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String encrypt(String rawPassword) {

        return bCryptPasswordEncoder.encode(rawPassword);
    }

    public static Boolean validate(String rawPassword) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

        return rawPassword.matches(regex);
    }

    public static Boolean matches(String rawPassword, String encodedPassword) {

        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
