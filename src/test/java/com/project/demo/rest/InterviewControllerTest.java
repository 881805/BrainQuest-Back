package com.project.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.rest.interview.InterviewController;
import com.project.demo.service.InterviewService;
import com.project.demo.logic.entity.auth.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InterviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InterviewService interviewService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /interviews: retorna 200 cuando el service responde OK")
    void simulateInterview_shouldReturn200WhenServiceOk() throws Exception {

        Game req = new Game();

        ResponseEntity<Object> serviceResponse =
                ResponseEntity.ok("ok");

        Mockito.when(interviewService.simulateInterview(any(Game.class), any()))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ok")));

        verify(interviewService, times(1)).simulateInterview(any(Game.class), any());
    }

    @Test
    @DisplayName("POST /interviews: retorna 500 cuando ocurre Exception general")
    void simulateInterview_shouldReturn500OnException() throws Exception {

        Game req = new Game();

        Mockito.when(interviewService.simulateInterview(any(Game.class), any()))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(
                        "Error en simulación de entrevista: boom"
                )));

        verify(interviewService, times(1)).simulateInterview(any(Game.class), any());
    }

    @Test
    @DisplayName("POST /interviews/feedback: retorna 200 cuando el service responde OK")
    void generateInterviewFeedback_shouldReturn200WhenServiceOk() throws Exception {

        Game req = new Game();

        ResponseEntity<Object> serviceResponse =
                ResponseEntity.status(HttpStatus.OK).body("feedback-ok");

        Mockito.when(interviewService.generateInterviewFeedback(any(Game.class), any()))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/interviews/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("feedback-ok")));

        verify(interviewService, times(1)).generateInterviewFeedback(any(Game.class), any());
    }

    @Test
    @DisplayName("POST /interviews/feedback: retorna 500 cuando ocurre Exception general")
    void generateInterviewFeedback_shouldReturn500OnException() throws Exception {

        Game req = new Game();

        Mockito.when(interviewService.generateInterviewFeedback(any(Game.class), any()))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/interviews/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(
                        "Error al generar feedback: boom"
                )));

        verify(interviewService, times(1)).generateInterviewFeedback(any(Game.class), any());
    }
}