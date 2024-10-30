package com.kboticket.service.login;

import com.kboticket.config.jwt.JwtTokenProvider;
import com.kboticket.domain.User;
import com.kboticket.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class LoginServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private LoginService loginService;
    @Mock
    private User user;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@naver.com");
        user.setPassword("password"); // 해시된 비밀번호 설정
    }
}