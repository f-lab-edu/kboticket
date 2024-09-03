package com.kboticket.service.user;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.*;
import com.kboticket.dto.TokenDto;
import com.kboticket.dto.user.UserDto;
import com.kboticket.dto.user.UserInfoDto;
import com.kboticket.dto.user.UserPasswordDto;
import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.*;
import com.kboticket.repository.terms.TermsRepository;
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
    public void signup(SignupRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmpassword();
        String verificationKey = request.getVerificationKey();

        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new KboTicketException(ErrorCode.EMAIL_DUPLICATTE);
        }

        // password 와 confirmpassword 비교
        if (!password.equals(confirmPassword)) {
            throw new KboTicketException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 인증키 확인
        String phone = jwtTokenProvider.getPhoneFromToken(verificationKey);
        if (phone == null) {
            throw new KboTicketException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .phone(phone)
                .build();

        List<Agreed> agreedList = request.getTerms().stream()
                .map(t -> {
                    TermsPk termsPk = new TermsPk(t.getTitle(), t.getVersion());
                    Terms terms = termsRepository.findById(termsPk)
                            .orElseThrow(() -> new KboTicketException(ErrorCode.TERM_NOT_FOUND_EXCEPTION));

                    return new Agreed(terms, user, LocalDateTime.now());
                })
                .collect(Collectors.toList());

        user.setAgreedList(agreedList);
        userRepository.save(user);
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

    public UserDto getUserDto(String email) {
        User user = getUserByEmail(email);

        return UserDto.builder()
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_USER));
    }

    // user 정보 변경
    public void updateUserInfo(String email, UserInfoDto userInfoDto) {
        User user = getUserByEmail(email);

        Address newAddress = Address.builder()
                .city(userInfoDto.getCity())
                .street(userInfoDto.getStreet())
                .zipcode(userInfoDto.getZipcode())
                .build();

        user.setAddress(newAddress);
        userRepository.save(user);
    }

    // password 변경
    public void updatePassword(UserPasswordDto userPasswordDto) {
        String email = userPasswordDto.getEmail();
        String currentPassword = userPasswordDto.getCurrentPassword();
        String newPassword = userPasswordDto.getNewPassword();
        String confirmPassword = userPasswordDto.getConfirmPassword();

        User user = getUserByEmail(email);

        // 기존 비밀 번호 확인
        if (!checkPassword(email, currentPassword)) {
            throw new KboTicketException(ErrorCode.INCORRECT_PASSWORD);
        }
        // newPassword, confirmPassword 일치 여부 확인
        if (!newPassword.equals(confirmPassword)) {
            throw new KboTicketException(ErrorCode.PASSWORD_MISMATCH);
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // email 이 존재하는지
    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // phone 이 존재하는지
    public boolean isExistPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // 전화번호로 이메일 찾기
    public String findbyPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_USER));
        return user.getEmail();
    }

    // 기존 비밀 번호 확인
    public boolean checkPassword(String email, String inputPassword) {
        log.info("storedpassword : " + inputPassword);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        String storedPassword = optionalUser.get().getPassword();

        log.info("storedpassword : " + storedPassword);
        return bCryptPasswordEncoder.matches(inputPassword, storedPassword);
    }
}
