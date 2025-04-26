package com.project.demo.service;

import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AiConfigurationRepository aiConfigurationRepository;

    public ResponseEntity<?> simulateInterview(Game game, HttpServletRequest request) {
        User user = game.getWinner();

        StringBuilder promptConfig = new StringBuilder();
        List<AiConfiguration> aiConfigs = aiConfigurationRepository.findByUserId(user.getId());
        for (AiConfiguration config : aiConfigs) {
            promptConfig.append(", ").append(config.getConfiguracion());
        }

        List<Message> gameMessages = game.getConversation().getMessages();
        if (gameMessages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay mensajes en la conversación.");
        }
        Message lastMessage = gameMessages.get(gameMessages.size() - 1);
        messageRepository.save(lastMessage);

        String interviewPrompt = gameMessages.toString() +
                " Eres un entrevistador profesional. Basado en las respuestas anteriores y el escenario seleccionado, haz una nueva pregunta relacionada." +
                " No repitas preguntas, mantén el formato profesional, breve y relevante. Que tu respuesta sea solamente" +
                " una respuesta a lo que dijo el usuario + una pregunta relacionada. Configuración de IA: " + promptConfig;

        String reply = geminiService.getCompletion(interviewPrompt);
        reply = reply.length() > 1000 ? reply.substring(0, 1000) : reply;

        Message aiMessage = new Message();
        aiMessage.setContentText(reply);
        aiMessage.setConversation(game.getConversation());
        aiMessage.setIsSent(true);
        User gemini = userRepository.findByEmail("gemini.google@gmail.com").orElseThrow();
        aiMessage.setUser(gemini);
        messageRepository.save(aiMessage);

        if (game.getElapsedTurns() >= game.getMaxTurns()) {
            return generateInterviewFeedback(game, request);
        }

        incrementTurn(game);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void incrementTurn(Game game) {
        game.setElapsedTurns(game.getElapsedTurns() + 1);
        gameRepository.save(game);
    }

    public ResponseEntity<?> generateInterviewFeedback(Game game, HttpServletRequest request) {
        List<Message> messages = game.getConversation().getMessages();
        String conversationString = messages.toString();

        String feedbackPrompt = conversationString +
                " Genera un JSON con el siguiente formato: {feedback: '', score: 0}. " +
                "El feedback debe comenzar con 'Retroalimentación: ' e incluir una puntuación del 0 al 500 con el texto 'Tu puntuación es:'. En español, por favor.";

        String reply = geminiService.getCompletion(feedbackPrompt);
        String cleanedJson = reply.replaceAll("```json|```", "").trim();

        String feedback;
        Long score;
        try {
            JSONObject json = new JSONObject(cleanedJson);
            feedback = json.getString("feedback");
            score = json.getLong("score");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al interpretar JSON de feedback: " + e.getMessage());
        }

        Message feedbackMessage = new Message();
        feedbackMessage.setContentText(feedback);
        feedbackMessage.setConversation(game.getConversation());
        feedbackMessage.setIsSent(true);
        User gemini = userRepository.findByEmail("gemini.google@gmail.com").orElseThrow();
        feedbackMessage.setUser(gemini);
        messageRepository.save(feedbackMessage);

        Optional<Game> optionalGame = gameRepository.findById(game.getId());
        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Juego no encontrado.");
        }

        Game foundGame = optionalGame.get();
        foundGame.setIsOngoing(false);
        foundGame.setPointsEarnedPlayer1(score.intValue());
        gameRepository.save(foundGame);

        Optional<User> winnerUser = userRepository.findById(game.getWinner().getId());
        if (winnerUser.isPresent()) {
            User winner = winnerUser.get();
            winner.setExperience(winner.getExperience() + score);
            userRepository.save(winner);
        }

        return new ResponseEntity<>(foundGame, HttpStatus.CREATED);
    }

    public Page<Game> getAllInterviews(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

}