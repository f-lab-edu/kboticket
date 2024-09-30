package com.kboticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.controller.user.UserApiController;
import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.controller.user.dto.SmsRequestDto;
import com.kboticket.dto.TokenDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SmsSenderService;
import com.kboticket.service.terms.TermsService;
import com.kboticket.service.user.UserService;
import com.kboticket.service.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiControllerTest.class)
@ExtendWith(SpringExtension.class)
public class UserApiControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private TermsService termsService;
    @Mock
    private SmsSenderService smsSenderService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        UserApiController userApiController = new UserApiController(userService, termsService, smsSenderService, jwtTokenProvider);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userApiController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 인증 번호 발송 테스트")
    void sendSmsTest() throws Exception {
        // given
        String phone = "010-1111-1111";

        doNothing().when(smsSenderService).sendVeritificationKey(anyString());

        // when & then
        mockMvc.perform(post("/api/user/sms-send")
                .content(phone)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[FAIL] sms 인증 실패")
    void smsVerificationFailTest() throws Exception {
        // given
        SmsRequestDto requestDto = new SmsRequestDto("010-1234-5678", "123456");
        String json = new ObjectMapper().writeValueAsString(requestDto);

        given(smsSenderService.verifySms(requestDto)).willReturn(false);

        // when & then
        mockMvc.perform(post("/api/user/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[SUCCESS] 회원 가입")
    void signupSuccessTest() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("test@gmail.com")
            .password("123")
            .confirmpassword("123")
            .verificationKey("vertificationkey")
            .terms(new ArrayList<>())
            .build();

        String json = new ObjectMapper().writeValueAsString(request);

        given(termsService.checkAllMandatoryTermsAgreed(request.getTerms())).willReturn(true);

        UserDto userDto = UserDto.from(request);

        doNothing().when(userService).signup(userDto);

        // when & then
        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[FAIL] 회원 가입 실패")
    void signupFailedTest() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("test@gmail.com")
            .password("123")
            .confirmpassword("123")
            .verificationKey("vertificationkey")
            .terms(new ArrayList<>())
            .build();

        String json = new ObjectMapper().writeValueAsString(request);

        given(termsService.checkAllMandatoryTermsAgreed(request.getTerms())).willReturn(false);

        // when & then
        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[SUCCESS] 인증 번호 재발급")
    void reissuedSuccessTest() throws Exception {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        TokenDto tokenDto = new TokenDto("newAccessToken", "refreshToken");

        given(userService.reissueToken(request)).willReturn(tokenDto);

        // when & then
        mockMvc.perform(post("/api/user/reissued")
                .requestAttr("javax.servlet.request", request))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[FAIL] 인증 번호 재발급 실패")
    void reissuedFailureTest() throws Exception {
        // given
        HttpServletRequest request = new MockHttpServletRequest();

        willThrow(new KboTicketException(ErrorCode.FAILED_GENERATE_TOKEN))
                .given(userService)
                .reissueToken(any());

        // when & then
        mockMvc.perform(post("/api/user/reissued")
                .requestAttr("javax.servlet.request", request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[SUCCESS] 이메일 중복 검사")
    void checkDuplicateEmailSuccessTest() throws Exception {
        // given
        String email = "test@naver.com";
        given(userService.isExistEmail(email)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/user/check-email")
                .param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Fail] 이메일 중복 검사")
    void checkDuplicateEmailFailTest() throws Exception {
        // given
        String email = "test@naver.com";
        given(userService.isExistEmail(email)).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/user/check-email")
                .param("email", email))
                .andExpect(status().isConflict());
    }

}
