package com.kboticket.service;

import com.kboticket.common.constants.constants.SmsConstant;
import com.kboticket.dto.SmsRequestDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.CectificationNumMismatchException;
import com.kboticket.exception.PhoneDuplicateException;
import com.kboticket.exception.SmsSendFailureException;
import com.kboticket.repository.SmsCertification;
import com.kboticket.repository.UserRepository;
import com.kboticket.util.coolSms.SmsMessageTemplate;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsSenderService {

    private final SmsCertification smsCertification;
    private final UserRepository userRepository;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    public void sendSMS(String phone) {

        if (userRepository.existsByPhone(phone)) {
            throw new PhoneDuplicateException(ErrorCode.PHONE_DUPLICATE);
        }
        /*
        Message coolsms = new Message(apiKey, apiSecret);

        String certNumber = createRandomNumber();
        String content = makeSmsContent(certNumber);

        HashMap<String, String> params = makeParams(phone, content);

        try{
            JSONObject result = coolsms.send(params);
            System.out.println(result.toString());
            if (result.get("success_count").toString().equals("0")) {
                throw new SmsSendFailureException(ErrorCode.SMS_SEND_FAILED);
            }
        } catch (CoolsmsException exception) {
            exception.printStackTrace();
        }
        smsCertification.createSmsCertification(phone, certNumber);
        */

        // redis 임시 인증 번호 저장
        String testNumber = "12345";
        smsCertification.createSmsCertification(phone, testNumber);
    }

    private String createRandomNumber() {
        Random random = new Random();
        StringBuilder randomNumberBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumberBuilder.append(random.nextInt(10));
        }
        return randomNumberBuilder.toString();
    }

    private String makeSmsContent(String certNumber) {
        SmsMessageTemplate content = new SmsMessageTemplate();
        return content.builderCertificationContent(certNumber);
    }

    private HashMap<String, String> makeParams(String to, String content) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", fromNumber);
        params.put("type", SmsConstant.SMS_TYPE);
        params.put("app_version", SmsConstant.APP_VERSION);
        params.put("to", to);
        params.put("text", content);
        return params;
    }

    public boolean verifySms(SmsRequestDto smsRequestDto) {
        if (!isVerify(smsRequestDto)) {
            throw new CectificationNumMismatchException(ErrorCode.CERT_NUMBER_MISMATCH);
        }
        return smsCertification.deleteSmsCertification(smsRequestDto.getPhone());
    }

    private boolean isVerify(SmsRequestDto smsRequestDto) {

        return (smsCertification.hasKey(smsRequestDto.getPhone()) &&
                smsCertification.getSmsCertification(smsRequestDto.getPhone())
                        .equals(smsRequestDto.getCertificationNumber()));

    }



}
