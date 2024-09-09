package com.kboticket.controller.stadium;

import com.kboticket.service.seat.SeatService;
import com.kboticket.service.stadium.StadiumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
}