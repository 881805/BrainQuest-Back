package com.project.demo.service;

//import com.project.demo.logic.entity.game.Game;
//
//import com.project.demo.logic.entity.game.GameRepository;
//import com.project.demo.service.GameService;
//import com.project.demo.service.InterviewService;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class InterviewServiceTest {
//
//    @Mock
//    private GameRepository gameRepository;
//
//    @InjectMocks
//    private GameService gameService;
//
//    @InjectMocks
//    private InterviewService interviewService;
//
//
//    public InterviewServiceTest() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testIncrementTurn() {
//        Game game = new Game();
//        game.setElapsedTurns(5);
//
//        interviewService.incrementTurn(game);
//
//        assertEquals(6, game.getElapsedTurns());
//        verify(gameRepository, times(1)).save(game);
//    }
//}


import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InterviewServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private GeminiService geminiService;
    @Mock private ConversationRepository conversationRepository;
    @Mock private GameRepository gameRepository;
    @Mock private AiConfigurationRepository aiConfigurationRepository;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private InterviewService interviewService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper builders

    private User buildUser(Long id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setExperience(0L);
        return u;
    }

    private Message buildMessage(String content, User user) {
        Message m = new Message();
        m.setContentText(content);
        m.setUser(user);
        return m;
    }

    private Game buildGameWithConversation(User winner, List<Message> messages, int elapsedTurns, int maxTurns) {
        Conversation conv = new Conversation();
        conv.setMessages(messages);

        Game g = new Game();
        g.setId(99L);
        g.setWinner(winner);
        g.setConversation(conv);
        g.setElapsedTurns(elapsedTurns);
        g.setMaxTurns(maxTurns);
        g.setIsOngoing(true);
        return g;
    }

    // incrementTurn

    @Test
    public void testIncrementTurn() {
        Game game = new Game();
        game.setElapsedTurns(5);

        interviewService.incrementTurn(game);

        assertEquals(6, game.getElapsedTurns());
        verify(gameRepository, times(1)).save(game);
    }

    // simulateInterview

    @Test
    public void testSimulateInterview_WhenNoMessages_ReturnsBadRequest() {
        User winner = buildUser(1L, "winner@test.com");
        Game game = buildGameWithConversation(winner, new ArrayList<>(), 0, 5);

        when(aiConfigurationRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = interviewService.simulateInterview(game, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No hay mensajes en la conversación.", response.getBody());
        verifyNoInteractions(geminiService);
        verify(messageRepository, never()).save(any(Message.class));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testSimulateInterview_NormalFlow_SavesMessages_IncrementsTurn_ReturnsCreated() {
        User winner = buildUser(1L, "winner@test.com");
        User geminiUser = buildUser(2L, "gemini.google@gmail.com");

        // IA config
        AiConfiguration cfg1 = new AiConfiguration();
        cfg1.setConfiguracion("tono formal");
        when(aiConfigurationRepository.findByUserId(1L)).thenReturn(List.of(cfg1));

        // conversation messages
        Message m1 = buildMessage("Hola", winner);
        Message m2 = buildMessage("Mi respuesta a la primera pregunta", winner);
        List<Message> messages = new ArrayList<>(List.of(m1, m2));

        Game game = buildGameWithConversation(winner, messages, 0, 5);

        when(userRepository.findByEmail("gemini.google@gmail.com"))
                .thenReturn(Optional.of(geminiUser));

        when(geminiService.getCompletion(anyString()))
                .thenReturn("Respuesta IA + pregunta nueva");

        ResponseEntity<Object> response = interviewService.simulateInterview(game, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Debe guardar el último mensaje del usuario y el mensaje IA
        verify(messageRepository, atLeastOnce()).save(any(Message.class));
        verify(gameRepository, times(1)).save(game); // por incrementTurn

        assertEquals(1, game.getElapsedTurns());
    }

    @Test
    public void testSimulateInterview_TruncatesReplyTo1000Chars() {
        User winner = buildUser(1L, "winner@test.com");
        User geminiUser = buildUser(2L, "gemini.google@gmail.com");

        when(aiConfigurationRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("gemini.google@gmail.com")).thenReturn(Optional.of(geminiUser));

        // respuesta larga
        String longReply = "A".repeat(1500);
        when(geminiService.getCompletion(anyString())).thenReturn(longReply);

        Message m1 = buildMessage("respuesta usuario", winner);
        Game game = buildGameWithConversation(winner, new ArrayList<>(List.of(m1)), 0, 10);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        ResponseEntity<Object> response = interviewService.simulateInterview(game, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(messageRepository, atLeast(2)).save(messageCaptor.capture());
        List<Message> saved = messageCaptor.getAllValues();

        Message lastSaved = saved.get(saved.size() - 1);
        assertNotNull(lastSaved.getContentText());
        assertEquals(1000, lastSaved.getContentText().length());
    }

    @Test
    public void testSimulateInterview_WhenMaxTurnsReached_ReturnsFeedbackResponse() {
        User winner = buildUser(1L, "winner@test.com");
        User geminiUser = buildUser(2L, "gemini.google@gmail.com");

        when(aiConfigurationRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("gemini.google@gmail.com")).thenReturn(Optional.of(geminiUser));

        // simulateInterview primero crea un prompt para pregunta nueva (geminiService.getCompletion)
        when(geminiService.getCompletion(anyString())).thenReturn("{\"feedback\":\"Retroalimentación: ok. Tu puntuación es:\",\"score\":100}");

        // Juego ya en límite
        Message m1 = buildMessage("respuesta usuario", winner);
        Game game = buildGameWithConversation(winner, new ArrayList<>(List.of(m1)), 5, 5);

        // Para generateInterviewFeedback:
        when(gameRepository.findById(99L)).thenReturn(Optional.of(game));
        when(userRepository.findById(1L)).thenReturn(Optional.of(winner));

        ResponseEntity<Object> response = interviewService.simulateInterview(game, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(gameRepository, atLeastOnce()).save(any(Game.class));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    // generateInterviewFeedback

    @Test
    public void testGenerateInterviewFeedback_ValidJson_UpdatesGameAndUser_ReturnsCreated() {
        User winner = buildUser(1L, "winner@test.com");
        User geminiUser = buildUser(2L, "gemini.google@gmail.com");

        when(userRepository.findByEmail("gemini.google@gmail.com")).thenReturn(Optional.of(geminiUser));

        Message m1 = buildMessage("respuesta usuario", winner);
        Game game = buildGameWithConversation(winner, new ArrayList<>(List.of(m1)), 0, 5);

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{\"feedback\":\"Retroalimentación: Bien. Tu puntuación es:\",\"score\":250}");

        when(gameRepository.findById(99L)).thenReturn(Optional.of(game));
        when(userRepository.findById(1L)).thenReturn(Optional.of(winner));

        ResponseEntity<Object> response = interviewService.generateInterviewFeedback(game, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verifica que el juego queda finalizado y puntos asignados
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository, atLeastOnce()).save(gameCaptor.capture());

        Game savedGame = gameCaptor.getAllValues().get(gameCaptor.getAllValues().size() - 1);
        assertFalse(savedGame.getIsOngoing());
        assertEquals(250, savedGame.getPointsEarnedPlayer1());

        // Verifica experiencia del usuario
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());

        User savedWinner = userCaptor.getAllValues().get(userCaptor.getAllValues().size() - 1);
        assertEquals(250L, savedWinner.getExperience());
    }

    @Test
    public void testGenerateInterviewFeedback_InvalidJson_ReturnsInternalServerError() {
        User winner = buildUser(1L, "winner@test.com");
        when(userRepository.findByEmail("gemini.google@gmail.com"))
                .thenReturn(Optional.of(buildUser(2L, "gemini.google@gmail.com")));

        Message m1 = buildMessage("respuesta usuario", winner);
        Game game = buildGameWithConversation(winner, new ArrayList<>(List.of(m1)), 0, 5);

        when(geminiService.getCompletion(anyString()))
                .thenReturn("NO ES JSON");

        ResponseEntity<Object> response = interviewService.generateInterviewFeedback(game, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().startsWith("Error al interpretar JSON de feedback:"));
        verify(gameRepository, never()).findById(anyLong());
    }

    @Test
    public void testGenerateInterviewFeedback_GameNotFound_ReturnsNotFound() {
        User winner = buildUser(1L, "winner@test.com");
        when(userRepository.findByEmail("gemini.google@gmail.com"))
                .thenReturn(Optional.of(buildUser(2L, "gemini.google@gmail.com")));

        Message m1 = buildMessage("respuesta usuario", winner);
        Game game = buildGameWithConversation(winner, new ArrayList<>(List.of(m1)), 0, 5);

        when(geminiService.getCompletion(anyString()))
                .thenReturn("{\"feedback\":\"Retroalimentación: ok. Tu puntuación es:\",\"score\":120}");

        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = interviewService.generateInterviewFeedback(game, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Juego no encontrado.", response.getBody());
    }
}