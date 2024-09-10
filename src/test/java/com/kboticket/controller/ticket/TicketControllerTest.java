package com.kboticket.controller.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.common.CommonResponse;
import com.kboticket.controller.ticket.dto.TicketResponse;
import com.kboticket.dto.TicketDto;
import com.kboticket.enums.TicketStatus;
import com.kboticket.service.ticket.TicketService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TicketController.class)
@ExtendWith(SpringExtension.class)
public class TicketControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        TicketController ticketController = new TicketController(ticketService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 티켓 상세 정보 조회")
    void viewTest() throws Exception {
        TicketDto ticketDto = TicketDto.builder()
                .id(1L)
                .ticketNumber("12342323")
                .name("LG VS 한화")
                .price(1000)
                .isCanceled(true)
                .status(TicketStatus.ISSUED)
                .build();

        // given
        given(ticketService.getTicket(anyLong())).willReturn(ticketDto);

        // when
        String response = new ObjectMapper().writeValueAsString(new CommonResponse<>(TicketResponse.from(ticketDto)));

        mockMvc.perform(get("/tickets/1")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(response));

        // then
        verify(ticketService, times(1)).getTicket(1L);
    }

    @Test
    @DisplayName("[SUCCESS] 티켓 취소")
    void cancelTest() {

    }
}