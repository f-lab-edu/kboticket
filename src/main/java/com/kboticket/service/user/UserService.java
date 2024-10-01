package com.kboticket.service.user;

import com.kboticket.common.util.PasswordUtils;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.*;
import com.kboticket.dto.TermsDto;
import com.kboticket.dto.TokenDto;
import com.kboticket.service.user.dto.UserDto;
import com.kboticket.service.user.dto.UserInfoDto;
import com.kboticket.service.user.dto.UserPasswordDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.repository.UserRepository;
import com.kboticket.repository.terms.TermsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TermsRepository termsRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(UserDto userDto) {
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        String confirmPassword = userDto.getConfirmpassword();
        String verificationKey = userDto.getVertificationKey();

        checkDuplicateEmail(email);
        validatePassword(password, confirmPassword);

        String phone = getPhoneFromKey(verificationKey);

        User user = createUser(email, password, phone);

        List<Agreed> agreedList = saveAgreedTerms(user, userDto.getTerms());

        user.setAgreedList(agreedList);

        userRepository.save(user);
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new KboTicketException(ErrorCode.EMAIL_DUPLICATTE);
        }
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!PasswordUtils.validate(password)) {
            throw new KboTicketException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if (!password.equals(confirmPassword)) {
            throw new KboTicketException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private String getPhoneFromKey(String verificationKey) {
        String phone = jwtTokenProvider.getPhoneFromToken(verificationKey);
        if (phone == null) {
            throw new KboTicketException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return phone;
    }

    private User createUser(String email, String password, String phone) {
        return User.builder()
            .email(email)
            .password(PasswordUtils.encrypt(password))
            .phone(phone)
            .build();
    }

    private List<Agreed> saveAgreedTerms(User user, List<TermsDto> termDtos) {
        return termDtos.stream()
            .map(termDto -> {
                Terms terms = termsRepository.findById(new TermsPk(termDto.getTitle(), termDto.getVersion()))
                    .orElseThrow(() -> new KboTicketException(ErrorCode.TERM_NOT_FOUND_EXCEPTION));
                return new Agreed(terms, user, LocalDateTime.now());
            })
            .collect(Collectors.toList());
    }

    public boolean verifyPassword(String email, String inputPassword) {
        String endcodePassword = getUserByEmail(email).getPassword();
        return PasswordUtils.matches(inputPassword, endcodePassword);
    }

    public TokenDto reissueToken(HttpServletRequest request) throws Exception {
        String refreshToken = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);

        return new TokenDto(newAccessToken, refreshToken);
    }

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

    public void updatePassword(UserPasswordDto userPasswordDto) {
        String email = userPasswordDto.getEmail();
        String currentPassword = userPasswordDto.getCurrentPassword();
        String newPassword = userPasswordDto.getNewPassword();
        String confirmPassword = userPasswordDto.getConfirmPassword();

        User user = getUserByEmail(email);

        validateCurrentPassword(currentPassword, user.getPassword());
        validatePassword(newPassword, confirmPassword);


        user.setPassword(PasswordUtils.encrypt(newPassword));
        userRepository.save(user);
    }

    private void validateCurrentPassword(String inputPassword, String storedPassword) {
        if (!PasswordUtils.matches(inputPassword, storedPassword)) {
            throw new KboTicketException(ErrorCode.INCORRECT_PASSWORD);
        }
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

    public boolean isExistEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    public boolean isExistPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public String findEmailbyPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new KboTicketException(ErrorCode.NOT_FOUND_USER));

        return user.getEmail();
    }
}
