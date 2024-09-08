package com.kboticket.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.controller.user.UserController;
import com.kboticket.controller.user.dto.ChangePasswordRequest;
import com.kboticket.controller.user.dto.UpdateUserRequest;
import com.kboticket.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("[SUCCESS] Password 일치 여부")
    void verifyPasswordSuccessTest() throws Exception {
        // given
        String email = "test@naver.com";
        String password = "1111";

        given(authentication.getName()).willReturn(email);
        given(userService.verifyPassword(anyString(), anyString())).willReturn(true);

        // when & then
        mockMvc.perform(post("/users/verify-password")
                .principal(authentication)
                .content(password)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus()))
                .andExpect(status().isOk());

        verify(userService).verifyPassword(email, password);
    }

    @Test
    @DisplayName("[FAIL] 비밀번호 일치 여부 테스트")
    void verifyPasswordFailTest() throws Exception {
        // given
        String email = "test@naver.com";
        String password = "failtest";

        given(authentication.getName()).willReturn(email);
        doNothing().when(userService).updateUserInfo(anyString(), any());

        // when & then
        mockMvc.perform(post("/users/verify-password")
                .principal(authentication)
                .content(password)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus()))
                .andExpect(status().isOk());

        verify(userService).verifyPassword(email, password);
    }

    @Test
    @DisplayName("[SUCCESS 유저 정보 변경")
    void updateInfoTest() throws Exception{
        // given
        String email = "test@naver.com";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .city("Seoul")
                .build();

        String json = new ObjectMapper().writeValueAsString(request);

        given(authentication.getName()).willReturn(email);
        doNothing().when(userService).updateUserInfo(anyString(), any());

        // when & then
        mockMvc.perform(post("/users/update-info")
                .principal(authentication)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Success] 유저 password 변경")
    void updatePasswordTest() throws Exception{
        // given
        String email = "test@naver.com";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("1111")
                .newPassword("0000")
                .confirmPassword("1234")
                .build();

        String json = new ObjectMapper().writeValueAsString(request);

        given(authentication.getName()).willReturn(email);
        doNothing().when(userService).updatePassword(any());

        // when/ then
        mockMvc.perform(post("/users/update-password")
                .principal(authentication)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
