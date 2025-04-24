package com.project.demo.rest.interview;

import com.project.demo.gemini.GeminiService;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

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

    public InterviewController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
    }

    @Transactional
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> simulateInterview(@RequestBody Game game, HttpServletRequest request) {
        User user = game.getWinner();

        String promptConfig="";
        List<AiConfiguration> aiConfigs = aiConfigurationRepository.findByUserId(user.getId());
        for(AiConfiguration config : aiConfigs){
            promptConfig += ", ";
            promptConfig += config.getConfiguracion();
        }

        List<Message> gameMessages = game.getConversation().getMessages();
        Message messageToAdd = null;
        for (Message message : gameMessages) {
            if (message.getId() == gameMessages.size()) {
                messageToAdd = message;
            }
        }
        messageRepository.save(messageToAdd);

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

    private void incrementTurn(Game game) {
        game.setElapsedTurns(game.getElapsedTurns() + 1);
        gameRepository.save(game);
    }

    public ResponseEntity<?> generateInterviewFeedback(@RequestBody Game game, HttpServletRequest request) {
        List<Message> messages = game.getConversation().getMessages();
        String conversationString = messages.toString();

        String feedbackPrompt = conversationString +
                " Genera un JSON con el siguiente formato: {feedback: '', score: 0}. " +
                "El feedback debe comenzar con 'Retroalimentación: ' e incluir una puntuación del 0 al 500 con el texto 'Tu puntuación es:'. En español, por favor.";

        String reply = geminiService.getCompletion(feedbackPrompt);
        String cleanedJson = reply.replaceAll("```json|```", "").trim();

        String feedback = "";
        Long score = 0L;
        try {
            JSONObject json = new JSONObject(cleanedJson);
            feedback = json.getString("feedback");
            score = json.getLong("score");
        } catch (JSONException e) {
            System.out.println(e);
        }

        Message feedbackMessage = new Message();
        feedbackMessage.setContentText(feedback);
        feedbackMessage.setConversation(game.getConversation());
        feedbackMessage.setIsSent(true);
        User gemini = userRepository.findByEmail("gemini.google@gmail.com").orElseThrow();
        feedbackMessage.setUser(gemini);
        messageRepository.save(feedbackMessage);

        Optional<Game> optionalGame = gameRepository.findById(game.getId());
        Game foundGame = null;
        if (optionalGame.isPresent()) {
            foundGame = optionalGame.get();
            foundGame.setIsOngoing(false);
            gameRepository.save(foundGame);
        }

        Long winnerId = game.getWinner().getId();
        Optional<User> winnerUser = userRepository.findById(winnerId);
        foundGame.setPointsEarnedPlayer1(Math.toIntExact(score));
        winnerUser.get().setExperience(score+winnerUser.get().getExperience());
        userRepository.save(winnerUser.get());
        return new ResponseEntity<>(foundGame, HttpStatus.CREATED);
    }
}