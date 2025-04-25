package com.project.demo;

import com.project.demo.logic.entity.typing.Typing;
import com.project.demo.logic.entity.typing.TypingRepository;
import com.project.demo.rest.typing.TypingController;
import com.project.demo.service.GeminiService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TypingTest {

    @Mock
    private TypingRepository typingRepository;

    @Mock
    private GeminiService geminiService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TypingController typingController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateTypingExercise() {
        // Arrange
        Typing typingRequest = new Typing();
        typingRequest.setCategory("animales");
        typingRequest.setDifficulty("fácil");

        String geminiResponse = """
            {
              "text": "El gato duerme en el sofá mientras la lluvia cae.",
              "timeLimit": 60,
              "hints": ["Animal doméstico", "Clima"]
            }
        """;

        when(geminiService.getCompletion(anyString())).thenReturn(geminiResponse);

        Typing expectedTyping = new Typing();
        expectedTyping.setText("El gato duerme en el sofá mientras la lluvia cae.");
        expectedTyping.setTimeLimit(60);
        expectedTyping.setHints(Arrays.asList("Animal doméstico", "Clima"));
        expectedTyping.setCategory("animales");
        expectedTyping.setDifficulty("fácil");

        when(typingRepository.save(any(Typing.class))).thenReturn(expectedTyping);

        // Act
        ResponseEntity<?> response = typingController.generateTypingExercise(typingRequest);

        // Assert
        assertEquals(201, response.getStatusCodeValue());

        Typing actualTyping = (Typing) response.getBody();
        assertNotNull(actualTyping);
        assertEquals("El gato duerme en el sofá mientras la lluvia cae.", actualTyping.getText());
        assertEquals(60, actualTyping.getTimeLimit());
        assertEquals("animales", actualTyping.getCategory());
        assertEquals("fácil", actualTyping.getDifficulty());
        assertEquals(Arrays.asList("Animal doméstico", "Clima"), actualTyping.getHints());
    }
}
