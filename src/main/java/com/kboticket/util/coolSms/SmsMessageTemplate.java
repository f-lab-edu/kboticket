package com.kboticket.util.coolSms;

public class SmsMessageTemplate {
    public String builderCertificationContent(String certificationNumber) {

        StringBuilder builder = new StringBuilder();
        builder.append("[KBO] 인증번호는 ");
        builder.append(certificationNumber);
        builder.append(" 입니다.");

        return builder.toString();
    }
}
