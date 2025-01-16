package com.kboticket.controller.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.controller.QueueService;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.controller.user.UserApiController;
import com.kboticket.service.game.GameService;
import com.kboticket.service.game.dto.GameDetailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(GameControllerTest.class)
public class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;
    @Mock
    private QueueService queueService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private GameController gameController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameController = new GameController(gameService, queueService, redisTemplate);
        this.mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 경기 목록 리스트")
    void testGameList() throws Exception {
        GameSearchRequest request = GameSearchRequest.builder().build();
        GameSearchResponse response = new GameSearchResponse();

        String json = new ObjectMapper().writeValueAsString(request);

        when(gameService.getGameList(any(GameSearchRequest.class), anyString(), anyInt())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/game/list")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json) // 요청 JSON 추가
            .param("cursor", "someCursorId")
            .param("limit", "10"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경기 상세 조회")
    public void testGameById() throws Exception {
        GameDetailDto detailDto = GameDetailDto.builder().build();
        given(gameService.findById(anyLong())).willReturn(detailDto);

        mockMvc.perform(get("/game/{gameId}", 1L))
                .andExpect(status().isOk());

        verify(gameService, times(1)).findById(anyLong());
    }
}