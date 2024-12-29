package com.kboticket.config;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.utils.PasswordUtils;
import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.User;
import com.kboticket.repository.UserRepository;
import com.kboticket.service.login.LogoutService;
import com.kboticket.service.login.dto.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(WebSecurityConfig.class)
public class WebSecurityConfigTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserDetailsService userService;

    @MockBean
    LogoutService logoutService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    MockMvc mockMvc;

    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    WebApplicationContext webApplicationContext;

    Authentication authentication;

    LoginDto loginDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(this.webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

        loginDto = LoginDto.builder()
            .username("test123@naver.com")
            .password("test123**")
            .build();
    }


    @Test
    @DisplayName("로그인 성공 테스트")
    public void login_success() throws Exception {
        String body = new ObjectMapper().writeValueAsString(loginDto);

        UserDetails userDetails = User.builder()
            .email("test123@naver.com")
            .password(PasswordUtils.encrypt("test123**"))
            .build();

        when(userService.loadUserByUsername("test123@naver.com")).thenReturn(userDetails);

        ResultActions resultActions = mockMvc.perform(post("/login")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))
            .andDo(print());

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void login_fail() throws Exception {
        String body = new ObjectMapper().writeValueAsString(loginDto);

        UserDetails userDetails = User.builder()
            .email("test123@naver.com")
            .password(PasswordUtils.encrypt("worngpassword123!"))
            .build();

        when(userService.loadUserByUsername("test123@naver.com")).thenReturn(userDetails);

        ResultActions resultActions = mockMvc.perform(post("/login")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))
            .andDo(print());

        resultActions.andExpect(status().isUnauthorized());
    }

}
