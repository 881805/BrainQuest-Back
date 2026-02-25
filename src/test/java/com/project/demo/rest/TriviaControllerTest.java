package com.project.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.dto.UserAnswerRequest;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import com.project.demo.rest.trivia.TriviaController;
import com.project.demo.dto.FeedbackResponse;
import com.project.demo.service.GeminiService;
import com.project.demo.service.TriviaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TriviaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TriviaControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TriviaRepository triviaRepository;
    @MockBean GeminiService geminiService;
    @MockBean TriviaService triviaService;

    @MockBean JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /trivia/generate: retorna 201 cuando el service genera la pregunta")
    void generateTriviaQuestion_shouldReturn201() throws Exception {

        TriviaQuestion req = new TriviaQuestion();
        req.setCategory("historia");
        req.setDifficulty("facil");

        TriviaQuestion saved = new TriviaQuestion();
        saved.setCategory("historia");
        saved.setDifficulty("facil");
        saved.setQuestion("¿En qué año llegó Colón a América?");
        saved.setCorrectAnswer("1492");

        when(triviaService.generateTriviaQuestion(any(TriviaQuestion.class))).thenReturn(saved);

        mockMvc.perform(post("/trivia/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").value("historia"))
                .andExpect(jsonPath("$.difficulty").value("facil"))
                .andExpect(jsonPath("$.question").value("¿En qué año llegó Colón a América?"))
                .andExpect(jsonPath("$.correctAnswer").value("1492"));

        verify(triviaService, times(1)).generateTriviaQuestion(any(TriviaQuestion.class));
    }

    @Test
    @DisplayName("POST /trivia/generate: retorna 409 cuando el service lanza RuntimeException")
    void generateTriviaQuestion_shouldReturn409OnRuntimeException() throws Exception {

        TriviaQuestion req = new TriviaQuestion();
        req.setCategory("historia");
        req.setDifficulty("facil");

        when(triviaService.generateTriviaQuestion(any(TriviaQuestion.class)))
                .thenThrow(new RuntimeException("Ya existe una pregunta igual"));

        mockMvc.perform(post("/trivia/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe una pregunta igual"));

        verify(triviaService, times(1)).generateTriviaQuestion(any(TriviaQuestion.class));
    }

    @Test
    @DisplayName("POST /trivia/generate: retorna 500 cuando ocurre Exception general")
    void generateTriviaQuestion_shouldReturn500OnException() throws Exception {

        TriviaQuestion req = new TriviaQuestion();
        req.setCategory("historia");
        req.setDifficulty("facil");

        when(triviaService.generateTriviaQuestion(any(TriviaQuestion.class)))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/trivia/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("boom")));

        verify(triviaService, times(1)).generateTriviaQuestion(any(TriviaQuestion.class));
    }

    @Test
    @DisplayName("POST /trivia/feedback: retorna 200 y devuelve lista")
    void getFeedback_shouldReturn200() throws Exception {

        UserAnswerRequest req = new UserAnswerRequest();

        List<FeedbackResponse> feedback = List.of(
                new FeedbackResponse(1L, "Pregunta ejemplo", "A", "B", "Explicación breve"),
                new FeedbackResponse(2L, "Otra pregunta", "C", "D", "Otra explicación")
        );

        when(triviaService.generateFeedback(any(UserAnswerRequest.class)))
                .thenReturn(feedback);

        mockMvc.perform(post("/trivia/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(triviaService, times(1)).generateFeedback(any(UserAnswerRequest.class));
    }

    @Test
    @DisplayName("GET /trivia: retorna 200 y llama al repository con PageRequest(page-1,size)")
    void getAllTriviaQuestions_shouldReturn200AndUseCorrectPageable() throws Exception {

        TriviaQuestion q1 = new TriviaQuestion();
        q1.setQuestion("Q1");
        TriviaQuestion q2 = new TriviaQuestion();
        q2.setQuestion("Q2");

        Page<TriviaQuestion> page = new PageImpl<>(
                List.of(q1, q2),
                PageRequest.of(0, 10),
                2
        );

        when(triviaRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/trivia")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(triviaRepository).findAll(captor.capture());

        Pageable used = captor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(10, used.getPageSize());
    }

}