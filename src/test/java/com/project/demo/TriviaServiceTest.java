package com.project.demo;


import com.project.demo.logic.entity.trivia.Option;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import com.project.demo.service.GeminiService;
import com.project.demo.service.TriviaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private TriviaService triviaService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateTriviaQuestion_Successful() {
        // Given
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

        TriviaQuestion parsedQuestion = new TriviaQuestion();
        parsedQuestion.setQuestion("¿Cuál es el planeta más cercano al Sol?");
        parsedQuestion.setOptions(List.of(
                new Option("Venus"),
                new Option("Tierra"),
                new Option("Mercurio"),
                new Option("Marte")
        ));
        parsedQuestion.setCorrectAnswer("Mercurio");
        parsedQuestion.setCategory("Ciencia");
        parsedQuestion.setDifficulty("Fácil");

        // When
        when(triviaRepository.findByCategoryAndDifficulty("Ciencia", "Fácil")).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any())).thenReturn(Optional.empty());
        when(triviaRepository.save(any())).thenReturn(parsedQuestion);

        // Then
        TriviaQuestion result = triviaService.generateTriviaQuestion(request);

        assertNotNull(result);
        assertEquals("¿Cuál es el planeta más cercano al Sol?", result.getQuestion());
        assertEquals("Mercurio", result.getCorrectAnswer());
        verify(triviaRepository).save(parsedQuestion);
    }

    @Test
    public void testGenerateTriviaQuestion_DuplicateQuestion_ThrowsException() {
        // Given
        TriviaQuestion request = new TriviaQuestion();
        request.setCategory("Historia");
        request.setDifficulty("Media");

        String jsonResponse = """
        {
            "question": "¿Quién fue el primer presidente de EE.UU.?",
            "options": ["Lincoln", "Washington", "Jefferson", "Adams"],
            "correctAnswer": "Washington"
        }
        """;

        TriviaQuestion parsedQuestion = new TriviaQuestion();
        parsedQuestion.setQuestion("¿Quién fue el primer presidente de EE.UU.?");
        parsedQuestion.setOptions(List.of(
                new Option("Lincoln"),
                new Option("Washington"),
                new Option("Jefferson"),
                new Option("Adams")
        ));
        parsedQuestion.setCorrectAnswer("Washington");
        parsedQuestion.setCategory("Historia");
        parsedQuestion.setDifficulty("Media");

        when(triviaRepository.findByCategoryAndDifficulty(any(), any())).thenReturn(List.of());
        when(geminiService.getCompletion(anyString())).thenReturn(jsonResponse);
        when(triviaRepository.findByQuestionAndCategoryAndDifficulty(any(), any(), any()))
                .thenReturn(Optional.of(parsedQuestion));

        // Then
        assertThrows(RuntimeException.class, () -> {
            triviaService.generateTriviaQuestion(request);
        });

        verify(triviaRepository, never()).save(any());
    }
}
