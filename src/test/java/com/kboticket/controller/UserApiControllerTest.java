package com.kboticket.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.controller.user.UserApiController;
import com.kboticket.controller.user.dto.SignupRequest;
import com.kboticket.controller.user.dto.SmsRequest;
import com.kboticket.controller.user.dto.SmsRequestDto;
import com.kboticket.dto.TokenDto;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.SmsSenderService;
import com.kboticket.service.terms.TermsService;
import com.kboticket.service.user.UserService;
import com.kboticket.service.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(UserApiControllerTest.class)
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
    @InjectMocks
    private UserApiController userApiController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userApiController = new UserApiController(userService, termsService, smsSenderService, jwtTokenProvider);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userApiController).build();
    }

    @Test
    @DisplayName("인증 번호 발송 테스트 - 성공")
    void sendSmsTest() throws Exception {
        // given
        String phone = "01011111111";
        String json = new ObjectMapper().writeValueAsString(phone);

        doNothing().when(smsSenderService).sendVeritificationKey(anyString());

        mockMvc.perform(post("/api/user/sms-send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus()));

    }

    @Test
    @DisplayName("sms 인증 - 성공")
    void smsVerificationSuccessTest() throws Exception {
        SmsRequest request = SmsRequest.builder()
            .phone("01011112222")
            .certificationNumber("123456")
            .build();

        SmsRequestDto requestDto = SmsRequestDto.from(request);
        String json = new ObjectMapper().writeValueAsString(request);

        when(smsSenderService.verifySms(any(SmsRequestDto.class))).thenReturn(true);

        // when & then
        mockMvc.perform(post("/api/user/verify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());

        }

    @Test
    @DisplayName("SMS 인증 실패 테스트")
    void smsVerification_invalidCode() throws Exception {
        // given
        SmsRequest request = SmsRequest.builder()
            .phone("01011112222")
            .certificationNumber("12345")
            .build();

        SmsRequestDto requestDto = SmsRequestDto.from(request);
        String json = new ObjectMapper().writeValueAsString(request);

        when(smsSenderService.verifySms(requestDto)).thenReturn(false);

        Assertions.assertThatThrownBy(() ->
            mockMvc.perform(post("/api/user/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)))
            .hasCause(new KboTicketException(ErrorCode.INVALID_VERIFICATION_CODE));
    }


    @Test
    @DisplayName("회원 가입 - 성공")
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
    @DisplayName("회원 가입 실패 - 약관 미동의")
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

        when(termsService.checkAllMandatoryTermsAgreed(request.getTerms())).thenReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() ->
            mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            ).hasCause(new KboTicketException(ErrorCode.NOT_CHECKED_MANDATORY_TERMS));
    }

    @Test
    @DisplayName("인증 번호 재발급 - 성공")
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
    @DisplayName("인증 번호 재발급 - 실패")
    void reissuedFailureTest() throws Exception {
        // given
        HttpServletRequest request = new MockHttpServletRequest();

        willThrow(new KboTicketException(ErrorCode.FAILED_GENERATE_TOKEN))
                .given(userService)
                .reissueToken(any());

        // when & then
        Assertions.assertThatThrownBy(() ->
            mockMvc.perform(post("/api/user/reissued")
                .requestAttr("javax.servlet.request", request))
        ).hasCause(new KboTicketException(ErrorCode.FAILED_GENERATE_TOKEN));


    }

    @Test
    @DisplayName("이메일 중복 검사 - 성공")
    void checkDuplicateEmailSuccessTest() throws Exception {
        // given
        String email = "test@naver.com";
        given(userService.isExistEmail(email)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/user/check-email")
                .param("email", email))
                .andExpect(status().isOk());
    }


}
