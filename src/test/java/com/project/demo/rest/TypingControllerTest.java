package com.project.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.typing.Typing;
import com.project.demo.rest.typing.TypingController;
import com.project.demo.service.TypingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TypingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TypingControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TypingService typingService;

    @MockBean JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /typing/generate: retorna 201 cuando el service genera el ejercicio")
    void generateTypingExercise_shouldReturn201() throws Exception {
        Typing req = new Typing();
        req.setCategory("animales");
        req.setDifficulty("facil");

        Typing saved = new Typing();
        saved.setCategory("animales");
        saved.setDifficulty("facil");
        saved.setText("El gato duerme en el sofá.");
        saved.setTimeLimit(60);
        saved.setHints(List.of("Animal doméstico", "Acción"));

        when(typingService.generateTypingExercise(any(Typing.class))).thenReturn(saved);

        mockMvc.perform(post("/typing/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.category").value("animales"))
                .andExpect(jsonPath("$.difficulty").value("facil"))
                .andExpect(jsonPath("$.text").value("El gato duerme en el sofá."))
                .andExpect(jsonPath("$.timeLimit").value(60))
                .andExpect(jsonPath("$.hints").isArray());

        verify(typingService, times(1)).generateTypingExercise(any(Typing.class));
    }

    @Test
    @DisplayName("POST /typing/generate: retorna 500 cuando el service lanza excepción")
    void generateTypingExercise_shouldReturn500OnError() throws Exception {
        Typing req = new Typing();
        req.setCategory("animales");
        req.setDifficulty("facil");

        when(typingService.generateTypingExercise(any(Typing.class)))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/typing/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        verify(typingService, times(1)).generateTypingExercise(any(Typing.class));
    }

    @Test
    @DisplayName("GET /typing/all: retorna 200 y llama al service con PageRequest(page-1,size)")
    void getPaginatedTypingExercises_shouldReturn200() throws Exception {
        Typing t1 = new Typing(); t1.setText("T1");
        Typing t2 = new Typing(); t2.setText("T2");

        Page<Typing> page = new PageImpl<>(List.of(t1, t2));
        when(typingService.getPaginatedExercises(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/typing/all")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(typingService).getPaginatedExercises(captor.capture());

        Pageable used = captor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(10, used.getPageSize());
    }
}