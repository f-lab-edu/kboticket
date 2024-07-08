package com.kboticket.util.coolSms;

public class SmsTemplate {

    public static final String CERT_TEMPLATE = "[KBO] 인증번호는 %s 입니다.";
    public static final String TEMP_PASSWORD_TEMPLATE = "[KBO] 변경된 비밀번호는 %s 입니다.";

    public static String builderContent(String template, String number) {
        return String.format(template, number);
    }
}
