package com.kboticket.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.dto.ReservationDto;
import com.kboticket.enums.SeatLevel;
import com.kboticket.service.reserve.ReservationService;
import com.kboticket.service.seat.SeatService;
import com.kboticket.service.seat.dto.SeatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@ExtendWith(SpringExtension.class)
public class ReservationControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SeatService seatService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        ReservationController reservationController = new ReservationController(reservationService, seatService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 좌석 예약 ")
    void reserve() throws Exception {
        // given
        String email = authentication.getName();
        given(email).willReturn("user@example.com");

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

        given(seatService.getSeatDto(1L)).willReturn(seatDto1);
        given(seatService.getSeatDto(2L)).willReturn(seatDto2);

        // when
        ReservationDto reservationDto = new ReservationDto(Set.of(1L, 2L));

        String json = new ObjectMapper().writeValueAsString(reservationDto);

        // then
        mockMvc.perform(post("/reservations")
                .contentType(APPLICATION_JSON)
                .param("gameId", "1")
                .content(json)
                .principal(authentication)) // 인증 정보 전달
                .andExpect(status().isOk());

        verify(reservationService, times(1))
                .selectSeat(Set.of(1L, 2L), 1L, "user@example.com");

    }
}