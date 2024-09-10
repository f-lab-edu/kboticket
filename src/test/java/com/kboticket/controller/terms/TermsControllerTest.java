package com.kboticket.controller.terms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kboticket.dto.TermsDto;
import com.kboticket.enums.TermsType;
import com.kboticket.service.terms.TermsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TermsController.class)
@ExtendWith(SpringExtension.class)
public class TermsControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TermsService termsService;

    @BeforeEach
    void setUp() {
        TermsController termsController = new TermsController(termsService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(termsController).build();
    }

    @Test
    @DisplayName("[SUCCESS] 약관 추가")
    public void testCreateNewTerms() throws Exception {
        TermsDto termsDto = TermsDto.builder()
                .title("term 1")
                .content("content 1")
                .version("1.0")
                .type(TermsType.SIGNIN)
                .mandatory(true)
                .build();

        String json = new ObjectMapper().writeValueAsString(termsDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/terms/new")
                .content(json)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(termsService, times(1)).createNewTerms(termsDto);
    }

    @Test
    @DisplayName("[SUCCESS] 약관 목록 조회")
    public void testTermsList() throws Exception {
        // given
        TermsDto term1 = new TermsDto("Term 1", "1.0", "content1", true, TermsType.SIGNIN);
        TermsDto term2 = new TermsDto("Term 2", "2.0", "content2", false, TermsType.SIGNIN);

        given(termsService.findLatestTermsByTitle(TermsType.SIGNIN))
                .willReturn(Arrays.asList(term1, term2));

        // when/ then
        mockMvc.perform(post("/terms/latest")
                .param("type", String.valueOf(TermsType.SIGNIN))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}