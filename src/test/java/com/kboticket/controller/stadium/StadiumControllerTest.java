package com.kboticket.controller.stadium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.CommonResponse;
import com.kboticket.controller.stadium.dto.StadiumDetailResponse;
import com.kboticket.enums.SeatLevel;
import com.kboticket.service.seat.SeatService;
import com.kboticket.service.seat.dto.SeatDto;
import com.kboticket.service.stadium.StadiumService;
import com.kboticket.service.stadium.dto.StadiumDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StadiumController.class)
@ExtendWith(SpringExtension.class)
public class StadiumControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private StadiumService stadiumService;

    @MockBean
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        StadiumController stadiumController = new StadiumController(stadiumService, seatService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(stadiumController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 경기장 상세 정보 조회")
    void viewTest() throws Exception {
        StadiumDto stadiumDto = StadiumDto.builder()
                .id("HH")
                .name("한화")
                .address("대전")
                .build();

        SeatDto seatDto1 = SeatDto.builder()
                .id(1L)
                .level(SeatLevel.VIP.level)
                .block("101")
                .seatX(1)
                .seatY(1)
                .seatZ(1)
                .build();

        SeatDto seatDto2 = SeatDto.builder()
                .id(2L)
                .level(SeatLevel.VIP.level)
                .block("101")
                .seatX(1)
                .seatY(1)
                .seatZ(2)
                .build();

        // given
        given(stadiumService.view(anyString())).willReturn(stadiumDto);
        given(seatService.getSeatsByStadium(anyString())).willReturn(Arrays.asList(seatDto1, seatDto2));

        // when
        StadiumDetailResponse stadiumDetailResponse = StadiumDetailResponse.of(stadiumDto, Arrays.asList(seatDto1, seatDto2));
        String response = new ObjectMapper().writeValueAsString(new CommonResponse<>(stadiumDetailResponse));

        // then
        mockMvc.perform(get("/stadium/view")
                .param("stadiumId", "HH")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(response));

        // 서비스 호출 확인
        verify(stadiumService, times(1)).view("HH");
        verify(seatService, times(1)).getSeatsByStadium("HH");
    }
}