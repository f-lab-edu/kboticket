package com.kboticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.controller.login.LoginController;
import com.kboticket.controller.login.dto.LoginRequest;
import com.kboticket.dto.login.LoginDto;
import com.kboticket.service.login.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@ExtendWith(SpringExtension.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        LoginController loginController = new LoginController(loginService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 로그인 성공")
    void loginTest() throws Exception {
        String username = "test@naver.com";
        String password = "1111";

        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        LoginDto loginDto = LoginDto.from(request);
        String json = new ObjectMapper().writeValueAsString(loginDto);

        doNothing().when(loginService).login(any(LoginDto.class));

        mockMvc.perform(post("/login")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
