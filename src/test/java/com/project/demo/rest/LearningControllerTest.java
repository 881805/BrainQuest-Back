package com.project.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.dto.LearningScenarioRequest;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.learning.LearningScenario;
import com.project.demo.rest.learning.LearningController;
import com.project.demo.service.LearningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LearningController.class)
@AutoConfigureMockMvc(addFilters = false)
class LearningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LearningService learningService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("POST /learning/generate: retorna 201 cuando se genera escenario correctamente")
    void generateScenario_shouldReturn201() throws Exception {
        LearningScenarioRequest req = new LearningScenarioRequest();


        LearningScenario scenario = new LearningScenario();

        when(learningService.generateScenario(any(LearningScenarioRequest.class)))
                .thenReturn(scenario);

        mockMvc.perform(post("/learning/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(learningService, times(1)).generateScenario(any(LearningScenarioRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /learning/generate: retorna 500 cuando ocurre Exception")
    void generateScenario_shouldReturn500OnException() throws Exception {
        LearningScenarioRequest req = new LearningScenarioRequest();

        when(learningService.generateScenario(any(LearningScenarioRequest.class)))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/learning/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Error generando escenario: boom"
                )));

        verify(learningService, times(1)).generateScenario(any(LearningScenarioRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /learning/topic/{topic}: retorna 200 y lista paginada")
    void getScenariosByTopic_shouldReturn200() throws Exception {
        String topic = "inyeccion";
        Pageable pageable = PageRequest.of(0, 10);
        Page<LearningScenario> page = new PageImpl<>(
                List.of(new LearningScenario()),
                pageable,
                1
        );

        when(learningService.getScenariosByTopic(eq(topic), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/learning/topic/{topic}", topic)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Learning scenarios retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalPages").value(1))
                .andExpect(jsonPath("$.meta.totalElements").value(1))
                .andExpect(jsonPath("$.meta.pageNumber").value(1))
                .andExpect(jsonPath("$.meta.pageSize").value(10));

        verify(learningService, times(1)).getScenariosByTopic(eq(topic), any(Pageable.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /learning/feedback/{id}: retorna 200 con feedback")
    void getFeedback_shouldReturn200() throws Exception {
        Long id = 10L;
        String userAnswer = "A";

        when(learningService.processFeedback(eq(id), eq(userAnswer)))
                .thenReturn(Map.of("correct", true, "message", "Bien"));

        mockMvc.perform(post("/learning/feedback/{id}", id)
                        .param("userAnswer", userAnswer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.message").value("Bien"));

        verify(learningService, times(1)).processFeedback(eq(id), eq(userAnswer));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /learning/feedback/{id}: retorna 404 cuando el service lanza IllegalArgumentException")
    void getFeedback_shouldReturn404OnIllegalArgument() throws Exception {
        Long id = 99L;
        String userAnswer = "B";

        when(learningService.processFeedback(eq(id), eq(userAnswer)))
                .thenThrow(new IllegalArgumentException("Scenario not found"));

        mockMvc.perform(post("/learning/feedback/{id}", id)
                        .param("userAnswer", userAnswer))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Scenario not found")));

        verify(learningService, times(1)).processFeedback(eq(id), eq(userAnswer));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /learning/feedback/{id}: retorna 500 cuando ocurre Exception general")
    void getFeedback_shouldReturn500OnException() throws Exception {
        Long id = 10L;
        String userAnswer = "C";

        when(learningService.processFeedback(eq(id), eq(userAnswer)))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/learning/feedback/{id}", id)
                        .param("userAnswer", userAnswer))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Error al generar feedback: boom"
                )));

        verify(learningService, times(1)).processFeedback(eq(id), eq(userAnswer));
    }
}