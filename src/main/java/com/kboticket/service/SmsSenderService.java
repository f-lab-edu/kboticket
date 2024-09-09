package com.kboticket.service;

import com.kboticket.common.constants.SmsConstant;
import com.kboticket.domain.User;
import com.kboticket.controller.user.dto.SmsRequestDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.SmsCertification;
import com.kboticket.repository.UserRepository;
import com.kboticket.common.util.coolSms.SmsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsSenderService {

    private final SmsCertification smsCertification;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    public void sendSMS(String phone, String key, String content) {

        /*
        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = makeParams(phone, content);

        try{
            JSONObject result = coolsms.send(params);
            System.out.println(result.toString());
            if (result.get("success_count").toString().equals("0")) {
                throw new SmsSendFailureException(ErrorCode.SMS_SEND_FAILED);
            }
        } catch (SmsSendFailureException | CoolsmsException exception) {
            exception.printStackTrace();
        }
        smsCertification.createSmsCertification(phone, key);
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
            throw new KboTicketException(ErrorCode.CERT_NUMBER_MISMATCH);
        }
        return smsCertification.deleteSmsCertification(smsRequestDto.getPhone());
    }

    private boolean isVerify(SmsRequestDto smsRequestDto) {

        return (smsCertification.hasKey(smsRequestDto.getPhone()) &&
                smsCertification.getSmsCertification(smsRequestDto.getPhone())
                        .equals(smsRequestDto.getCertificationNumber()));
    }

    public void sendVeritificationKey(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new KboTicketException(ErrorCode.PHONE_DUPLICATE);
        }

        String certNumber = createRandomNumber();
        String content = SmsTemplate.builderContent(SmsTemplate.CERT_TEMPLATE, certNumber);

        sendSMS(phone, certNumber, content);
    }


    public String sendResetPassword(String phone) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user = optionalUser.get();

        String tempPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9);
        String content = SmsTemplate.builderContent(SmsTemplate.TEMP_PASSWORD_TEMPLATE, tempPassword);

        log.info("tempPassword======>" + tempPassword);

        String encodedPassword = bCryptPasswordEncoder.encode(tempPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        sendSMS(phone, tempPassword, content);

        return tempPassword;
    }
}
