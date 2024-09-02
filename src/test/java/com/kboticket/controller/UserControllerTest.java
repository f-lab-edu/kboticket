package com.kboticket.controller;


import com.kboticket.controller.user.UserController;
import com.kboticket.enums.ErrorCode;
import com.kboticket.exception.KboTicketException;
import com.kboticket.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
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
    @DisplayName("[SUCCESS] 비밀번호 일치여부 테스트")
    void verifyPasswordSuccessTest() throws Exception {
        // given
        String email = "test@naver.com";
        String password = "1111";

        when(authentication.getName()).thenReturn(email);
        when(userService.checkPassword(email, password)).thenReturn(true);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/users/verify-password")
                .principal(authentication)
                .content(password)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus()))
                .andExpect(status().isOk());

        verify(userService).checkPassword(email, password);
    }

    @Test
    @DisplayName("[FAIL] 비밀번호 일치여부 테스트")
    void verifyPasswordFailTest() throws Exception {
        // given
        String email = "test@naver.com";
        String password = "failtest";

        when(authentication.getName()).thenReturn(email);
        when(userService.checkPassword(email, password)).thenReturn(false);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/users/verify-password")
                .principal(authentication)
                .content(password)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("Response status: " + result.getResponse().getStatus()))
                .andExpect(status().isOk());

        verify(userService).checkPassword(email, password);
    }






}
