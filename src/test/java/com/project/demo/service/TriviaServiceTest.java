package com.project.demo.service;


//import com.project.demo.logic.entity.trivia.Option;
//import com.project.demo.logic.entity.trivia.TriviaQuestion;
//import com.project.demo.logic.entity.trivia.TriviaRepository;
//import com.project.demo.service.GeminiService;
//import com.project.demo.service.TriviaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class TriviaServiceTest {
//
//    @Mock
//    private TriviaRepository triviaRepository;
//
//    @Mock
//    private GeminiService geminiService;
//
//    @InjectMocks
//    private TriviaService triviaService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGenerateTriviaQuestion_Successful() {
//        // Given
//        TriviaQuestion request = new TriviaQuestion();
//        request.setCategory("Ciencia");
//        request.setDifficulty("Fácil");
//
//        String jsonResponse = """
//        {
//            "question": "¿Cuál es el planeta más cercano al Sol?",
//            "options": ["Venus", "Tierra", "Mercurio", "Marte"],
//            "correctAnswer": "Mercurio"
//        }
//        """;
//
//        TriviaQuestion parsedQuestion = new TriviaQuestion();
//        parsedQuestion.setQuestion("¿Cuál es el planeta más cercano al Sol?");
//        parsedQuestion.setOptions(List.of(
//                new Option("Venus"),
//                new Option("Tierra"),
//                new Option("Mercurio"),
//                new Option("Marte")
//        ));
//        parsedQuestion.setCorrectAnswer("Mercurio");
//        parsedQuestion.setCategory("Ciencia");
//        parsedQuestion.setDifficulty("Fácil");
//
//        // When
//        when(triviaRepository.findByCategoryAndDifficulty("Ciencia", "Fácil")).thenReturn(List.of());
//        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
//        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any())).thenReturn(Optional.empty());
//        when(triviaRepository.save(any())).thenReturn(parsedQuestion);
//
//        // Then
//        TriviaQuestion result = triviaService.generateTriviaQuestion(request);
//
//        assertNotNull(result);
//        assertEquals("¿Cuál es el planeta más cercano al Sol?", result.getQuestion());
//        assertEquals("Mercurio", result.getCorrectAnswer());
//        verify(triviaRepository).save(parsedQuestion);
//    }
//
//    @Test
//    public void testGenerateTriviaQuestion_DuplicateQuestion_ThrowsException() {
//        // Given
//        TriviaQuestion request = new TriviaQuestion();
//        request.setCategory("Historia");
//        request.setDifficulty("Media");
//
//        String jsonResponse = """
//        {
//            "question": "¿Quién fue el primer presidente de EE.UU.?",
//            "options": ["Lincoln", "Washington", "Jefferson", "Adams"],
//            "correctAnswer": "Washington"
//        }
//        """;
//
//        TriviaQuestion parsedQuestion = new TriviaQuestion();
//        parsedQuestion.setQuestion("¿Quién fue el primer presidente de EE.UU.?");
//        parsedQuestion.setOptions(List.of(
//                new Option("Lincoln"),
//                new Option("Washington"),
//                new Option("Jefferson"),
//                new Option("Adams")
//        ));
//        parsedQuestion.setCorrectAnswer("Washington");
//        parsedQuestion.setCategory("Historia");
//        parsedQuestion.setDifficulty("Media");
//
//        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
//        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
//        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
//                .thenReturn(Optional.of(parsedQuestion));
//
//        // Then
//        assertThrows(RuntimeException.class, () -> {
//            triviaService.generateTriviaQuestion(request);
//        });
//
//        verify(triviaRepository, never()).save(any());
//    }
//}


import com.project.demo.dto.FeedbackResponse;
import com.project.demo.dto.UserAnswerRequest;
import com.project.demo.logic.entity.trivia.Option;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private TriviaService triviaService;

    // A) generateTriviaQuestion()

    @Test
    @DisplayName("generateTriviaQuestion: happy path guarda y retorna la pregunta")
    void generateTriviaQuestion_shouldGenerateAndSave() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String jsonResponse = """
        {
          "question": "¿Cuál es el planeta más cercano al Sol?",
          "options": ["Venus", "Tierra", "Mercurio", "Marte"],
          "correctAnswer": "Mercurio"
        }
        """;

        when(triviaRepository.findByCategoryAndDifficulty("Ciencia", "Fácil")).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(triviaRepository.save(any(TriviaQuestion.class))).thenAnswer(inv -> inv.getArgument(0));

        TriviaQuestion result = triviaService.generateTriviaQuestion(request);

        assertNotNull(result);
        assertEquals("¿Cuál es el planeta más cercano al Sol?", result.getQuestion());
        assertEquals("Mercurio", result.getCorrectAnswer());
        assertEquals("Ciencia", result.getCategory());
        assertEquals("Fácil", result.getDifficulty());
        assertEquals(4, result.getOptions().size());

        verify(triviaRepository, times(1)).save(any(TriviaQuestion.class));
    }

    @Test
    @DisplayName("generateTriviaQuestion: marca correctamente la opción correcta (case-insensitive)")
    void generateTriviaQuestion_shouldMarkCorrectOption() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String jsonResponse = """
        {
          "question": "Q",
          "options": ["a", "b", "c", "d"],
          "correctAnswer": "C"
        }
        """;

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(triviaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TriviaQuestion result = triviaService.generateTriviaQuestion(request);

        long correctCount = result.getOptions().stream().filter(Option::isCorrect).count();
        assertEquals(1, correctCount);
        assertTrue(result.getOptions().stream().anyMatch(o -> o.getText().equals("c") && o.isCorrect()));
    }

    @Test
    @DisplayName("generateTriviaQuestion: el prompt incluye preguntas previas para evitar repetición")
    void generateTriviaQuestion_shouldIncludePreviousQuestionsInPrompt() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Historia");
        request.setDifficulty("Media");

        TriviaQuestion prev1 = new TriviaQuestion();
        prev1.setQuestion("Pregunta vieja 1");
        TriviaQuestion prev2 = new TriviaQuestion();
        prev2.setQuestion("Pregunta vieja 2");

        String jsonResponse = """
        { "question": "Nueva", "options": ["1","2","3","4"], "correctAnswer": "1" }
        """;

        when(triviaRepository.findByCategoryAndDifficulty("Historia", "Media"))
                .thenReturn(List.of(prev1, prev2));
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(triviaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        triviaService.generateTriviaQuestion(request);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiService).getCompletion(promptCaptor.capture());

        String prompt = promptCaptor.getValue();
        assertTrue(prompt.contains("Pregunta vieja 1"));
        assertTrue(prompt.contains("Pregunta vieja 2"));
        assertTrue(prompt.contains("Historia"));
        assertTrue(prompt.contains("Media"));
    }

    @Test
    @DisplayName("generateTriviaQuestion: limpia respuesta tipo ```json ... ``` antes de parsear")
    void generateTriviaQuestion_shouldCleanBackticksAndJsonPrefix() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String wrapped = """
        ```json
        { "question": "Nueva", "options": ["1","2","3","4"], "correctAnswer": "1" }
        ```
        """;

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(wrapped);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(triviaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TriviaQuestion result = triviaService.generateTriviaQuestion(request);

        assertEquals("Nueva", result.getQuestion());
        verify(triviaRepository).save(any());
    }

    @Test
    @DisplayName("generateTriviaQuestion: si Gemini no devuelve JSON (no empieza con '{') lanza excepción")
    void generateTriviaQuestion_shouldThrowWhenNotJson() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn("Hola, esto no es JSON");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> triviaService.generateTriviaQuestion(request));

        assertTrue(ex.getMessage().contains("Respuesta de Gemini no es JSON válido"));
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTriviaQuestion: si falta 'question' en JSON lanza RuntimeException (parse)")
    void generateTriviaQuestion_shouldThrowWhenMissingQuestionField() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String badJson = "{ \"options\": [\"1\",\"2\",\"3\",\"4\"], \"correctAnswer\": \"1\" }";

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(badJson);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> triviaService.generateTriviaQuestion(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTriviaQuestion: si falta 'options' en JSON lanza RuntimeException (parse)")
    void generateTriviaQuestion_shouldThrowWhenMissingOptionsField() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String badJson = "{ \"question\": \"Q\", \"correctAnswer\": \"1\" }";

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(badJson);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> triviaService.generateTriviaQuestion(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTriviaQuestion: si falta 'correctAnswer' en JSON lanza RuntimeException (parse)")
    void generateTriviaQuestion_shouldThrowWhenMissingCorrectAnswerField() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Ciencia");
        request.setDifficulty("Fácil");

        String badJson = "{ \"question\": \"Q\", \"options\": [\"1\",\"2\",\"3\",\"4\"] }";

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(badJson);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> triviaService.generateTriviaQuestion(request));

        assertTrue(ex.getMessage().contains("Error al parsear la respuesta de Gemini"));
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateTriviaQuestion: si ya existe pregunta similar lanza excepción y no guarda")
    void generateTriviaQuestion_shouldThrowWhenDuplicateExists() {
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Historia");
        request.setDifficulty("Media");

        String jsonResponse = """
        { "question": "Repetida", "options": ["1","2","3","4"], "correctAnswer": "1" }
        """;

        TriviaQuestion existing = new TriviaQuestion();
        existing.setQuestion("Repetida");
        existing.setCategory("Historia");
        existing.setDifficulty("Media");

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> triviaService.generateTriviaQuestion(request));

        assertTrue(ex.getMessage().contains("Ya existe una pregunta similar"));
        verify(triviaRepository, never()).save(any(TriviaQuestion.class));
    }

    // B) generateFeedback()

    @Test
    @DisplayName("generateFeedback: si la pregunta no existe (findById empty) la salta")
    void generateFeedback_shouldSkipWhenQuestionNotFound() {
        UserAnswerRequest req = new UserAnswerRequest();

        UserAnswerRequest.AnswerItem item = new UserAnswerRequest.AnswerItem();
        item.setQuestionId(999L);
        item.setUserAnswer("A");

        req.setAnswers(List.of(item));

        when(triviaRepository.findById(999L)).thenReturn(Optional.empty());

        List<FeedbackResponse> result = triviaService.generateFeedback(req);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(geminiService, never()).getCompletion(anyString());
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateFeedback: si la respuesta es correcta, no genera feedback ni guarda")
    void generateFeedback_shouldNotGenerateWhenCorrect() {
        TriviaQuestion q = new TriviaQuestion();
        q.setId(1L);
        q.setQuestion("Q");
        q.setCorrectAnswer("B");

        UserAnswerRequest req = new UserAnswerRequest();

        UserAnswerRequest.AnswerItem item = new UserAnswerRequest.AnswerItem();
        item.setQuestionId(1L);
        item.setUserAnswer("b"); // case-insensitive

        req.setAnswers(List.of(item));

        when(triviaRepository.findById(1L)).thenReturn(Optional.of(q));

        List<FeedbackResponse> result = triviaService.generateFeedback(req);

        assertTrue(result.isEmpty());
        verify(geminiService, never()).getCompletion(anyString());
        verify(triviaRepository, never()).save(any());
    }

    @Test
    @DisplayName("generateFeedback: si es incorrecta, genera feedback, guarda pregunta y retorna lista")
    void generateFeedback_shouldGenerateAndSaveWhenIncorrect() {
        TriviaQuestion q = new TriviaQuestion();
        q.setId(1L);
        q.setQuestion("Q");
        q.setCorrectAnswer("B");

        UserAnswerRequest req = new UserAnswerRequest();

        UserAnswerRequest.AnswerItem item = new UserAnswerRequest.AnswerItem();
        item.setQuestionId(1L);
        item.setUserAnswer("A");

        req.setAnswers(List.of(item));

        when(triviaRepository.findById(1L)).thenReturn(Optional.of(q));
        when(geminiService.getCompletion(anyString())).thenReturn("Explicación breve");
        when(triviaRepository.save(any(TriviaQuestion.class))).thenAnswer(inv -> inv.getArgument(0));

        List<FeedbackResponse> result = triviaService.generateFeedback(req);

        assertEquals(1, result.size());
        FeedbackResponse fr = result.get(0);

        assertEquals(1L, fr.getQuestionId());
        assertEquals("Q", fr.getQuestion());
        assertEquals("A", fr.getUserAnswer());
        assertEquals("B", fr.getCorrectAnswer());
        assertEquals("Explicación breve", fr.getFeedback());

        ArgumentCaptor<TriviaQuestion> captor = ArgumentCaptor.forClass(TriviaQuestion.class);
        verify(triviaRepository).save(captor.capture());
        assertEquals("Explicación breve", captor.getValue().getFeedback());
    }

    @Test
    @DisplayName("generateFeedback: limpia backticks/json del feedback antes de guardar")
    void generateFeedback_shouldCleanFeedback() {
        TriviaQuestion q = new TriviaQuestion();
        q.setId(2L);
        q.setQuestion("Q2");
        q.setCorrectAnswer("D");

        UserAnswerRequest req = new UserAnswerRequest();

        UserAnswerRequest.AnswerItem item = new UserAnswerRequest.AnswerItem();
        item.setQuestionId(2L);
        item.setUserAnswer("A");

        req.setAnswers(List.of(item));

        when(triviaRepository.findById(2L)).thenReturn(Optional.of(q));
        when(geminiService.getCompletion(anyString())).thenReturn("```json\nTexto limpio\n```");
        when(triviaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<FeedbackResponse> result = triviaService.generateFeedback(req);

        assertEquals(1, result.size());
        assertEquals("Texto limpio", result.get(0).getFeedback());

        ArgumentCaptor<TriviaQuestion> captor = ArgumentCaptor.forClass(TriviaQuestion.class);
        verify(triviaRepository).save(captor.capture());
        assertEquals("Texto limpio", captor.getValue().getFeedback());
    }

    @Test
    @DisplayName("generateFeedback: si Gemini falla, usa mensaje fallback y guarda")
    void generateFeedback_shouldUseFallbackWhenGeminiThrows() {
        TriviaQuestion q = new TriviaQuestion();
        q.setId(3L);
        q.setQuestion("Q3");
        q.setCorrectAnswer("C");

        UserAnswerRequest req = new UserAnswerRequest();

        UserAnswerRequest.AnswerItem item = new UserAnswerRequest.AnswerItem();
        item.setQuestionId(3L);
        item.setUserAnswer("A");

        req.setAnswers(List.of(item));

        when(triviaRepository.findById(3L)).thenReturn(Optional.of(q));
        when(geminiService.getCompletion(anyString())).thenThrow(new RuntimeException("boom"));
        when(triviaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<FeedbackResponse> result = triviaService.generateFeedback(req);

        assertEquals(1, result.size());
        assertEquals("No se pudo generar la explicación.", result.get(0).getFeedback());

        verify(triviaRepository, times(1)).save(any());
    }
}