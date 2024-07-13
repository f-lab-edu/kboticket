package com.kboticket.service;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.Agreed;
import com.kboticket.domain.Terms;
import com.kboticket.domain.TermsPk;
import com.kboticket.domain.User;
import com.kboticket.dto.TokenDto;
import com.kboticket.dto.UserSignupRequest;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.*;
import com.kboticket.repository.TermsRepository;
import com.kboticket.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signup(UserSignupRequest request) {
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
        if (phone == null) {
            throw new InvalidVerificationKeyException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phone(phone)
                .build();

        List<Agreed> agreedList = request.getTerms().stream()
                .map(t -> {

                    TermsPk termsPk = new TermsPk(t.getTitle(), t.getVersion());
                    Terms terms = termsRepository.findById(termsPk)
                            .orElseThrow(() -> new TermsNotFoundException(ErrorCode.TERM_NOT_FOUND_EXCEPTION));

                    return new Agreed(terms, user, LocalDateTime.now());
                })
                .collect(Collectors.toList());

        user.setAgreedList(agreedList);

        userRepository.save(user);
    }

    // 이메일이 존재하는지 확인
    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isExistPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // 전화번호로 이메일 찾기
    public String findbyPhone(String phone) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user = optionalUser.get();
        return user.getEmail();
    }

    public TokenDto reissue(HttpServletRequest request) throws Exception {
        String refreshToken = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);

        return new TokenDto(newAccessToken, refreshToken);

    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_USER));
    }



}
