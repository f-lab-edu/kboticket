package com.kboticket.controller.game;

import com.kboticket.config.kafka.producer.KafkaProducer;
import com.kboticket.controller.game.dto.GameSearchRequest;
import com.kboticket.controller.game.dto.GameSearchResponse;
import com.kboticket.service.game.GameService;
import com.kboticket.service.game.dto.GameDetailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@ExtendWith(SpringExtension.class)
public class GameControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private KafkaProducer producer;

    @BeforeEach
    void setUp() {
        GameController gameController = new GameController(gameService, producer);
        this.mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 경기 목록 리스트")
    void gameList() throws Exception {
        // given
        GameSearchResponse mockGameSearchResponse = new GameSearchResponse();
        given(gameService.getGameList(any(GameSearchRequest.class), anyString(), anyInt()))
                .willReturn(mockGameSearchResponse);

        // when/ then
        mockMvc.perform(get("/game/list")
                .contentType(APPLICATION_JSON)
                .content("{\"searchKey\":\"sample\"}")) // Adjust request body as necessary
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(gameService, times(1)).getGameList(any(GameSearchRequest.class), anyString(), anyInt());
    }

    @Test
    @DisplayName("[SUCCESS] 경기 상세")
    public void gameById() throws Exception {
        GameDetailDto detailDto = GameDetailDto.builder().build();
        given(gameService.findById(anyLong())).willReturn(detailDto);

        mockMvc.perform(get("/game/{gameId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(gameService, times(1)).findById(anyLong());
    }
}