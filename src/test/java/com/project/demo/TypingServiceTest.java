package com.project.demo;

//import com.project.demo.logic.entity.typing.Typing;
//import com.project.demo.logic.entity.typing.TypingRepository;
//import com.project.demo.rest.typing.TypingController;
//import com.project.demo.service.GeminiService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class TypingServiceTest {
//
//    @Mock
//    private TypingRepository typingRepository;
//
//    @Mock
//    private GeminiService geminiService;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @InjectMocks
//    private TypingController typingController;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGenerateTypingExercise() {
//        // Arrange
//        Typing typingRequest = new Typing();
//        typingRequest.setCategory("animales");
//        typingRequest.setDifficulty("fácil");
//
//        String geminiResponse = """
//            {
//              "text": "El gato duerme en el sofá mientras la lluvia cae.",
//              "timeLimit": 60,
//              "hints": ["Animal doméstico", "Clima"]
//            }
//        """;
//
//        when(geminiService.getCompletion(anyString())).thenReturn(geminiResponse);
//
//        Typing expectedTyping = new Typing();
//        expectedTyping.setText("El gato duerme en el sofá mientras la lluvia cae.");
//        expectedTyping.setTimeLimit(60);
//        expectedTyping.setHints(Arrays.asList("Animal doméstico", "Clima"));
//        expectedTyping.setCategory("animales");
//        expectedTyping.setDifficulty("fácil");
//
//        when(typingRepository.save(any(Typing.class))).thenReturn(expectedTyping);
//
//        // Act
//        ResponseEntity<?> response = typingController.generateTypingExercise(typingRequest);
//
//        // Assert
//        assertEquals(201, response.getStatusCodeValue());
//
//        Typing actualTyping = (Typing) response.getBody();
//        assertNotNull(actualTyping);
//        assertEquals("El gato duerme en el sofá mientras la lluvia cae.", actualTyping.getText());
//        assertEquals(60, actualTyping.getTimeLimit());
//        assertEquals("animales", actualTyping.getCategory());
//        assertEquals("fácil", actualTyping.getDifficulty());
//        assertEquals(Arrays.asList("Animal doméstico", "Clima"), actualTyping.getHints());
//    }
//}


import com.project.demo.logic.entity.typing.Typing;
import com.project.demo.logic.entity.typing.TypingRepository;
import com.project.demo.service.GeminiService;
import com.project.demo.service.TypingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TypingServiceTest {

    @Mock
    private TypingRepository typingRepository;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private TypingService typingService;

    // A) cleanGeminiResponse()

    @Test
    @DisplayName("cleanGeminiResponse: hace trim básico")
    void cleanGeminiResponse_shouldTrim() {
        String input = "   { \"a\": 1 }   ";
        String result = typingService.cleanGeminiResponse(input);
        assertEquals("{ \"a\": 1 }", result);
    }

    @Test
    @DisplayName("cleanGeminiResponse: elimina ``` del inicio y fin")
    void cleanGeminiResponse_shouldRemoveBackticks() {
        String input = "```{ \"a\": 1 }```";
        String result = typingService.cleanGeminiResponse(input);
        assertEquals("{ \"a\": 1 }", result);
    }

    @Test
    @DisplayName("cleanGeminiResponse: elimina prefijo 'json' sin importar mayúsculas")
    void cleanGeminiResponse_shouldRemoveJsonPrefixCaseInsensitive() {
        String input = "JSON\n{ \"a\": 1 }";
        String result = typingService.cleanGeminiResponse(input);
        assertEquals("{ \"a\": 1 }", result);
    }

    @Test
    @DisplayName("cleanGeminiResponse: elimina ``` y prefijo json combinado")
    void cleanGeminiResponse_shouldRemoveBackticksAndJsonPrefix() {
        String input = "```json\n{ \"a\": 1 }```";
        String result = typingService.cleanGeminiResponse(input);
        assertEquals("{ \"a\": 1 }", result);
    }

    @Test
    @DisplayName("cleanGeminiResponse: si no hay nada que limpiar, devuelve igual")
    void cleanGeminiResponse_shouldReturnSameWhenClean() {
        String input = "{ \"a\": 1 }";
        String result = typingService.cleanGeminiResponse(input);
        assertEquals("{ \"a\": 1 }", result);
    }

    // B) generateTypingExercise() happy path

    @Test
    @DisplayName("generateTypingExercise: happy path guarda y retorna el Typing parseado")
    void generateTypingExercise_shouldGenerateAndSaveTyping() {
        Typing request = new Typing();
        request.setCategory("Automotriz");
        request.setDifficulty("Fácil");

        String geminiRaw = "```json\n{ \"text\": \"Hola\", \"timeLimit\": 60, \"hints\": [\"h1\", \"h2\"] }\n```";
        when(geminiService.getCompletion(anyString())).thenReturn(geminiRaw);
        when(typingRepository.save(any(Typing.class))).thenAnswer(inv -> inv.getArgument(0));

        Typing result = typingService.generateTypingExercise(request);

        assertNotNull(result);
        assertEquals("Hola", result.getText());
        assertEquals(60, result.getTimeLimit());
        assertEquals(List.of("h1", "h2"), result.getHints());
        assertEquals("Automotriz", result.getCategory());
        assertEquals("Fácil", result.getDifficulty());

        verify(geminiService, times(1)).getCompletion(anyString());
        verify(typingRepository, times(1)).save(any(Typing.class));
    }

    @Test
    @DisplayName("generateTypingExercise: llama a Gemini con prompt que incluye category y difficulty")
    void generateTypingExercise_shouldCallGeminiWithCategoryAndDifficultyInPrompt() {
        Typing request = new Typing();
        request.setCategory("Frenos");
        request.setDifficulty("Difícil");

        String geminiRaw = "{ \"text\": \"Hola\", \"timeLimit\": 60, \"hints\": [\"h1\"] }";
        when(geminiService.getCompletion(anyString())).thenReturn(geminiRaw);
        when(typingRepository.save(any(Typing.class))).thenAnswer(inv -> inv.getArgument(0));

        typingService.generateTypingExercise(request);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiService).getCompletion(promptCaptor.capture());

        String usedPrompt = promptCaptor.getValue();
        assertTrue(usedPrompt.contains("Frenos"));
        assertTrue(usedPrompt.contains("Difícil"));
    }

    @Test
    @DisplayName("generateTypingExercise: lo que se guarda en repo tiene category y difficulty correctos")
    void generateTypingExercise_shouldSaveEntityWithCorrectCategoryAndDifficulty() {
        Typing request = new Typing();
        request.setCategory("Motor");
        request.setDifficulty("Media");

        String geminiRaw = "{ \"text\": \"Texto\", \"timeLimit\": 45, \"hints\": [\"h1\"] }";
        when(geminiService.getCompletion(anyString())).thenReturn(geminiRaw);
        when(typingRepository.save(any(Typing.class))).thenAnswer(inv -> inv.getArgument(0));

        typingService.generateTypingExercise(request);

        ArgumentCaptor<Typing> typingCaptor = ArgumentCaptor.forClass(Typing.class);
        verify(typingRepository).save(typingCaptor.capture());

        Typing saved = typingCaptor.getValue();
        assertEquals("Motor", saved.getCategory());
        assertEquals("Media", saved.getDifficulty());
    }

    @Test
    @DisplayName("generateTypingExercise: retorna lo que devuelva el repo")
    void generateTypingExercise_shouldReturnSavedEntityFromRepository() {
        Typing request = new Typing();
        request.setCategory("Suspensión");
        request.setDifficulty("Media");

        String geminiRaw = "{ \"text\": \"Texto\", \"timeLimit\": 60, \"hints\": [\"h1\"] }";
        when(geminiService.getCompletion(anyString())).thenReturn(geminiRaw);

        Typing repoReturn = new Typing();
        repoReturn.setText("Texto");
        repoReturn.setTimeLimit(60);
        repoReturn.setHints(List.of("h1"));
        repoReturn.setCategory("Suspensión");
        repoReturn.setDifficulty("Media");

        when(typingRepository.save(any(Typing.class))).thenReturn(repoReturn);

        Typing result = typingService.generateTypingExercise(request);

        assertSame(repoReturn, result);
    }

    // C) Parseo o validaciones negativas

    @Test
    @DisplayName("generateTypingExercise: si Gemini devuelve JSON inválido lanza RuntimeException")
    void generateTypingExercise_shouldThrowWhenInvalidJson() {
        Typing request = new Typing();
        request.setCategory("x");
        request.setDifficulty("y");

        when(geminiService.getCompletion(anyString())).thenReturn("NO ES JSON");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> typingService.generateTypingExercise(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(typingRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTypingExercise: si falta 'text' lanza RuntimeException")
    void generateTypingExercise_shouldThrowWhenMissingText() {
        Typing request = new Typing();
        request.setCategory("x");
        request.setDifficulty("y");

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{ \"timeLimit\": 60, \"hints\": [\"h1\"] }");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> typingService.generateTypingExercise(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(typingRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTypingExercise: si falta 'timeLimit' lanza RuntimeException")
    void generateTypingExercise_shouldThrowWhenMissingTimeLimit() {
        Typing request = new Typing();
        request.setCategory("x");
        request.setDifficulty("y");

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{ \"text\": \"Hola\", \"hints\": [\"h1\"] }");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> typingService.generateTypingExercise(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(typingRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTypingExercise: si falta 'hints' lanza RuntimeException")
    void generateTypingExercise_shouldThrowWhenMissingHints() {
        Typing request = new Typing();
        request.setCategory("x");
        request.setDifficulty("y");

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{ \"text\": \"Hola\", \"timeLimit\": 60 }");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> typingService.generateTypingExercise(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(typingRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTypingExercise: parsea hints y los guarda como lista")
    void generateTypingExercise_shouldParseHintsList() {
        Typing request = new Typing();
        request.setCategory("x");
        request.setDifficulty("y");

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{ \"text\": \"Hola\", \"timeLimit\": 60, \"hints\": [\"a\", \"b\", \"c\"] }");

        when(typingRepository.save(any(Typing.class))).thenAnswer(inv -> inv.getArgument(0));

        Typing result = typingService.generateTypingExercise(request);

        assertEquals(List.of("a", "b", "c"), result.getHints());
    }
}