package com.kboticket.service;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.Agreed;
import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import com.kboticket.domain.User;
import com.kboticket.dto.UserSignupRequest;
import com.kboticket.dto.UserSignupResponse;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.EmailDuplicateException;
import com.kboticket.exception.GenerateTempPwException;
import com.kboticket.exception.InvalidVerificationKeyException;
import com.kboticket.exception.PasswordMismatchException;
import com.kboticket.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SmsSenderService smsSenderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EntityManager entityManager;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailDuplicateException(ErrorCode.EMAIL_DUPLICATTE);
        }

        // password 와 confirmpassword 비교
        if (!request.getPassword().equals(request.getConfirmpassword())) {
            throw new PasswordMismatchException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 인증키 확인
        String phone = jwtTokenProvider.getPhoneFromToken(request.getVerificationKey());
        log.info("phone ======== > " + phone);
        if (phone == null) {
            throw new InvalidVerificationKeyException(ErrorCode.INVALID_VERIFICATION_KEY);
        }


        // 특정 유저 A 의 약관 동의 리스트
        List<Agreed> agreedList = request.getTerms().stream()
                .map(t -> {
                    Terms terms = entityManager.find(Terms.class, new TermsPk(t.getTitle(), t.getVersion()));
                    if (terms == null) {
                        terms = new Terms(t.getTitle(), t.getVersion());
                        entityManager.persist(terms); // 영속성 컨텍스트에 추가
                    }
                    return new Agreed(terms, null, LocalDateTime.now());
                })
                .collect(Collectors.toList());

        User user = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phone(phone)
                .build();

        agreedList.forEach(t -> {
            t.setUser(user);
            t.setAgreedDate(LocalDateTime.now());
        });

        user.setAgreedList(agreedList);
        // 유저 정보 저장
        userRepository.saveAndFlush(user);

        return UserSignupResponse.from(user);
    }

    // 이메일이 존재하는지 확인
    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 전화번호로 이메일 찾기
    public String findbyPhone(String phone) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user = optionalUser.get();
        return user.getEmail();
    }

    public void sendPasswordToEmail(String email) {
        String title = "password 임시 발급";
        String tempPassword = this.generatePassword();

    }

    private String generatePassword() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder tempPwBuilder = new StringBuilder();
            for (int i=0; i<length; i++) {
                tempPwBuilder.append(random.nextInt(10));
            }
            return tempPwBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new GenerateTempPwException(ErrorCode.GENERATE_TEMP_PW_ERR);
        }
    }

    // 인증코드를 검증하는 메서드
    /*
    pulic EmailVerificationResult verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return EmailVerificationResult.of(authResult);
    }
    */

}
