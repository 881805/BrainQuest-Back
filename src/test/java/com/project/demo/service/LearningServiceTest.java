package com.project.demo.service;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.demo.logic.entity.learning.LearningOption;
//import com.project.demo.logic.entity.learning.LearningScenario;
//import com.project.demo.service.LearningService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class LearningServiceTest {
//
//
//    @InjectMocks
//    private LearningService learningService;
//
//
//
//    @Test
//    public void testParseScenarioResponse() {
//        String json = """
//            {
//              "narrative": "You are a developer learning about Java Streams.",
//              "question": "Which method is used to filter elements in a Stream?",
//              "correctAnswer": "filter()",
//              "options": ["map()", "collect()", "filter()", "forEach()"]
//            }
//        """;
//
//        String topic = "Java Streams";
//        int step = 1;
//
//        LearningScenario scenario = learningService.parseScenarioResponse(json, topic, step);
//
//        assertEquals("You are a developer learning about Java Streams.", scenario.getNarrative());
//        assertEquals("Which method is used to filter elements in a Stream?", scenario.getQuestion());
//        assertEquals("filter()", scenario.getCorrectAnswer());
//        assertEquals(topic, scenario.getTopic());
//        assertEquals(step, scenario.getStepNumber());
//
//        List<LearningOption> options = scenario.getOptions();
//        assertEquals(4, options.size());
//
//        boolean correctFound = options.stream().anyMatch(LearningOption::isCorrect);
//        assertTrue(correctFound, "At least one option should be marked as correct");
//    }
//}


import com.project.demo.dto.LearningScenarioRequest;
import com.project.demo.logic.entity.learning.LearningOption;
import com.project.demo.logic.entity.learning.LearningRepository;
import com.project.demo.logic.entity.learning.LearningScenario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningServiceTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private LearningRepository learningRepository;

    @InjectMocks
    private LearningService learningService;

    // A) parseScenarioResponse()

    @Test
    @DisplayName("parseScenarioResponse: parsea JSON válido y marca opción correcta")
    void parseScenarioResponse_shouldParseValidJson() {
        String json = """
            {
              "narrative": "Historia",
              "question": "Pregunta",
              "correctAnswer": "B",
              "options": ["A", "B", "C", "D"]
            }
        """;

        LearningScenario scenario = learningService.parseScenarioResponse(json, "Tema", 1);

        assertEquals("Historia", scenario.getNarrative());
        assertEquals("Pregunta", scenario.getQuestion());
        assertEquals("B", scenario.getCorrectAnswer());
        assertEquals("Tema", scenario.getTopic());
        assertEquals(1, scenario.getStepNumber());
        assertEquals(4, scenario.getOptions().size());

        long correctCount = scenario.getOptions().stream().filter(LearningOption::isCorrect).count();
        assertEquals(1, correctCount);
        assertTrue(scenario.getOptions().stream().anyMatch(o -> o.getText().equals("B") && o.isCorrect()));
    }

    @Test
    @DisplayName("parseScenarioResponse: si JSON es inválido lanza RuntimeException")
    void parseScenarioResponse_shouldThrowWhenInvalidJson() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> learningService.parseScenarioResponse("NO JSON", "Tema", 1));

        assertTrue(ex.getMessage().contains("Error al parsear JSON de escenario"));
    }

    // B) generateScenario()

    @Test
    @DisplayName("generateScenario: happy path genera, guarda y retorna scenario")
    void generateScenario_shouldGenerateSaveAndReturn() {
        LearningScenarioRequest req = new LearningScenarioRequest();
        req.setTopic("Frenos");
        req.setStep(2);

        when(learningRepository.findByTopicOrderByStepNumberAsc("Frenos")).thenReturn(List.of());

        String geminiJson = """
        { "narrative":"N", "question":"Q", "options":["A","B","C","D"], "correctAnswer":"A" }
        """;

        when(geminiService.getCompletion(anyString())).thenReturn(geminiJson);
        when(learningRepository.save(any(LearningScenario.class))).thenAnswer(inv -> inv.getArgument(0));

        LearningScenario result = learningService.generateScenario(req);

        assertNotNull(result);
        assertEquals("Frenos", result.getTopic());
        assertEquals(2, result.getStepNumber());
        assertEquals("N", result.getNarrative());
        assertEquals("Q", result.getQuestion());
        assertEquals("A", result.getCorrectAnswer());

        verify(learningRepository, times(1)).save(any(LearningScenario.class));
    }

    @Test
    @DisplayName("generateScenario: incluye narrativa anterior cuando hay pasos previos")
    void generateScenario_shouldIncludeLastNarrativeInPrompt() {
        LearningScenarioRequest req = new LearningScenarioRequest();
        req.setTopic("Motor");
        req.setStep(3);

        LearningScenario prev1 = new LearningScenario();
        prev1.setNarrative("Narrativa 1");
        prev1.setQuestion("P1");

        LearningScenario prev2 = new LearningScenario();
        prev2.setNarrative("Narrativa 2");
        prev2.setQuestion("P2");

        when(learningRepository.findByTopicOrderByStepNumberAsc("Motor"))
                .thenReturn(List.of(prev1, prev2));

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{ \"narrative\":\"N\", \"question\":\"Q\", \"options\":[\"A\",\"B\",\"C\",\"D\"], \"correctAnswer\":\"A\" }");
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        learningService.generateScenario(req);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiService).getCompletion(promptCaptor.capture());

        String prompt = promptCaptor.getValue();
        assertTrue(prompt.contains("Motor"));
        assertTrue(prompt.contains("3"));
        assertTrue(prompt.contains("Narrativa 2"));
        assertTrue(prompt.contains("P1"));
        assertTrue(prompt.contains("P2"));
    }

    @Test
    @DisplayName("generateScenario: limpia respuesta tipo ```json ... ```")
    void generateScenario_shouldCleanBackticksAndJsonPrefix() {
        LearningScenarioRequest req = new LearningScenarioRequest();
        req.setTopic("Suspensión");
        req.setStep(1);

        when(learningRepository.findByTopicOrderByStepNumberAsc("Suspensión")).thenReturn(List.of());

        String wrapped = """
        ```json
        { "narrative":"N", "question":"Q", "options":["A","B","C","D"], "correctAnswer":"A" }
        ```
        """;

        when(geminiService.getCompletion(anyString())).thenReturn(wrapped);
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LearningScenario result = learningService.generateScenario(req);

        assertEquals("N", result.getNarrative());
        assertEquals("Q", result.getQuestion());
        verify(learningRepository).save(any());
    }

    @Test
    @DisplayName("generateScenario: si Gemini no devuelve JSON (no empieza con '{') lanza excepción")
    void generateScenario_shouldThrowWhenNotJson() {
        LearningScenarioRequest req = new LearningScenarioRequest();
        req.setTopic("x");
        req.setStep(1);

        when(learningRepository.findByTopicOrderByStepNumberAsc("x")).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn("Hola no JSON");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> learningService.generateScenario(req));

        assertTrue(ex.getMessage().contains("Respuesta de Gemini no es JSON válido"));
        verify(learningRepository, never()).save(any());
    }

    // C) processFeedback()

    @Test
    @DisplayName("processFeedback: si no existe escenario lanza IllegalArgumentException")
    void processFeedback_shouldThrowWhenNotFound() {
        when(learningRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> learningService.processFeedback(10L, "A"));

        assertTrue(ex.getMessage().contains("Escenario no encontrado"));
        verify(learningRepository, never()).save(any());
    }

    @Test
    @DisplayName("processFeedback: si respuesta es correcta guarda userAnswer y retorna mensaje")
    void processFeedback_shouldSaveAndReturnMessageWhenCorrect() {
        LearningScenario scenario = baseScenarioWithOptions("B");
        scenario.setId(1L);

        when(learningRepository.findById(1L)).thenReturn(Optional.of(scenario));
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = learningService.processFeedback(1L, "b"); // case-insensitive

        assertEquals("¡Respuesta correcta!", result.get("message"));
        ArgumentCaptor<LearningScenario> captor = ArgumentCaptor.forClass(LearningScenario.class);
        verify(learningRepository).save(captor.capture());
        assertEquals("b", captor.getValue().getUserAnswer());
        verify(geminiService, never()).getCompletion(anyString());
    }

    @Test
    @DisplayName("processFeedback: si respuesta es incorrecta bloquea opción seleccionada")
    void processFeedback_shouldBlockSelectedOptionWhenIncorrect() {
        LearningScenario scenario = baseScenarioWithOptions("D");
        scenario.setId(2L);

        when(learningRepository.findById(2L)).thenReturn(Optional.of(scenario));
        when(geminiService.getCompletion(anyString())).thenReturn("Explicación");
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = learningService.processFeedback(2L, "B");

        assertEquals("B", result.get("blockedOption"));
        assertEquals("Explicación", result.get("feedback"));

        LearningOption blocked = scenario.getOptions().stream()
                .filter(o -> o.getText().equalsIgnoreCase("B"))
                .findFirst()
                .orElseThrow();

        assertTrue(blocked.isBlocked());
    }

    @Test
    @DisplayName("processFeedback: si userAnswer no coincide con ninguna opción, no bloquea ninguna")
    void processFeedback_shouldNotBlockWhenAnswerNotInOptions() {
        LearningScenario scenario = baseScenarioWithOptions("A");
        scenario.setId(3L);

        when(learningRepository.findById(3L)).thenReturn(Optional.of(scenario));
        when(geminiService.getCompletion(anyString())).thenReturn("Explicación");
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        learningService.processFeedback(3L, "Z");

        boolean anyBlocked = scenario.getOptions().stream().anyMatch(LearningOption::isBlocked);
        assertFalse(anyBlocked);
    }

    @Test
    @DisplayName("processFeedback: limpia ``` y prefijo json del feedback antes de guardar")
    void processFeedback_shouldCleanFeedback() {
        LearningScenario scenario = baseScenarioWithOptions("A");
        scenario.setId(4L);

        when(learningRepository.findById(4L)).thenReturn(Optional.of(scenario));
        when(geminiService.getCompletion(anyString())).thenReturn("```json\nTexto limpio\n```");
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = learningService.processFeedback(4L, "B");

        assertEquals("Texto limpio", result.get("feedback"));

        ArgumentCaptor<LearningScenario> captor = ArgumentCaptor.forClass(LearningScenario.class);
        verify(learningRepository, atLeastOnce()).save(captor.capture());

        LearningScenario saved = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertEquals("Texto limpio", saved.getFeedback());
    }

    @Test
    @DisplayName("processFeedback: si Gemini retorna null o blank usa mensaje default")
    void processFeedback_shouldUseDefaultWhenGeminiReturnsBlank() {
        LearningScenario scenario = baseScenarioWithOptions("A");
        scenario.setId(5L);

        when(learningRepository.findById(5L)).thenReturn(Optional.of(scenario));
        when(geminiService.getCompletion(anyString())).thenReturn("   "); // blank
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = learningService.processFeedback(5L, "B");

        assertEquals("No se pudo generar una explicación válida. Intenta nuevamente.", result.get("feedback"));
    }

    @Test
    @DisplayName("processFeedback: si Gemini lanza excepción usa mensaje de error y guarda")
    void processFeedback_shouldUseErrorMessageWhenGeminiThrows() {
        LearningScenario scenario = baseScenarioWithOptions("A");
        scenario.setId(6L);

        when(learningRepository.findById(6L)).thenReturn(Optional.of(scenario));
        when(geminiService.getCompletion(anyString())).thenThrow(new RuntimeException("boom"));
        when(learningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = learningService.processFeedback(6L, "B");

        assertEquals("No se pudo generar la explicación debido a un error en el servicio.", result.get("feedback"));
        verify(learningRepository, atLeastOnce()).save(any());
    }

    // Helper

    private LearningScenario baseScenarioWithOptions(String correctAnswer) {
        LearningScenario s = new LearningScenario();
        s.setNarrative("N");
        s.setQuestion("Q");
        s.setCorrectAnswer(correctAnswer);
        s.setOptions(List.of(
                new LearningOption("A", "A".equalsIgnoreCase(correctAnswer), false),
                new LearningOption("B", "B".equalsIgnoreCase(correctAnswer), false),
                new LearningOption("C", "C".equalsIgnoreCase(correctAnswer), false),
                new LearningOption("D", "D".equalsIgnoreCase(correctAnswer), false)
        ));
        return s;
    }
}