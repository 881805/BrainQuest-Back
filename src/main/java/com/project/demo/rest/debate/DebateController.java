package com.project.demo.rest.debate;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/debates")
@RestController
public class DebateController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GeminiService geminiService;


    private AdminSeeder adminSeeder;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AiConfigurationRepository aiConfigurationRepository;

    public DebateController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
    }

    @Transactional
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> messageSent(@RequestBody Game game, HttpServletRequest request) {
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


        String replyPrompt = game.getConversation().getMessages().toString() + " En torno a la conversacion anterior da una respuesta " +
                "en torno al tema de debate considerando el mensaje del jugador. El id 2 representa al sistema. Si no hay id dos es el mensaje inicial y deberías responder solo con un contraataque a ese debate." +
                "Mantén la respuesta en menos de 5 oraciones y en torno a la conversación. Debe contener solo tu respuesta al debate no debería de decir 'Tema de discusión'" +
                " Configuración de IA: " + promptConfig;
        String reply = geminiService.getCompletion(replyPrompt);

        if (reply.length() > 1000) {
            reply = reply.substring(0, 1000);
        }

        Message replyMessage = new Message();
        replyMessage.setContentText(reply);
        replyMessage.setConversation(game.getConversation());
        replyMessage.setIsSent(true);
        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
        Long geminiUserId = optionalUser.get().getId();
        User gemini = userRepository.findById(geminiUserId).get();
        replyMessage.setUser(gemini);

        messageRepository.save(replyMessage);

        // Termina por judgeDebate si ya es el último turno
        if (game.getElapsedTurns() >= game.getMaxTurns()) {
            return judgeDebate(game, request);
        }

        String moderatorPrompt = game.getConversation().getMessages().toString() + " Eres un moderador de debate. En torno a la conversación anterior reconoce las respuestas con un pequeño de Gemini y del user1." +
                " Genera una pregunta para que los participantes sigan debatiendo con respecto al mismo tema," +
                " empezando con el texto 'Tema de discusión: y luego la pregunta o tema. contextText contiene los mensajes hablados. Responde solamente con 'Tema de discusión' y la pregunta."+ promptConfig;
        String moderatorReply = geminiService.getCompletion(moderatorPrompt);

        if (moderatorReply.length() > 1000) {
            moderatorReply = moderatorReply.substring(0, 1000);
        }

        Message replyMessageModerator = new Message();
        replyMessageModerator.setContentText(moderatorReply);
        replyMessageModerator.setConversation(game.getConversation());
        replyMessageModerator.setIsSent(true);
        replyMessageModerator.setUser(gemini);

        messageRepository.save(replyMessageModerator);
        patchElapsedTurns(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    public void patchElapsedTurns(Game game) {
        game.setElapsedTurns(game.getElapsedTurns() + 1);
        gameRepository.save(game);
    }

    public ResponseEntity<?> judgeDebate(@RequestBody Game game, HttpServletRequest request) throws JSONException {

        List<Message> messages = game.getConversation().getMessages();

        String conversationString = messages.toString();

        System.out.println(conversationString);

        String reply = geminiService.getCompletion(conversationString + " Send a json reply with the following format {feedback: '', score: 0} " +
                "El string de feedback empiezalo con la palabra 'Feeback: ' Y adicional mente tambien probee el score dentro de feedback diciendo 'La puntuacion es la siguiente:' y luego poniendo el score.Where the score is obtained by judging the performance of user1 and award them an ammount of points from 0 to 500. " +
                "Please keep your reply short sending only the number and a small sentence for feedback inside the json. contentText contiene los mensajes hablados. Mensajes en espanhol por favor"); //respuesta de ia genera

        JSONObject json = null;
        String feedback = null;
        Long score = null;


        String cleanedJson = reply.replaceAll("```json|```", "").trim();
        try {
            json = new JSONObject(cleanedJson);
            feedback = json.getString("feedback");
            score = json.getLong("score");

        } catch (JSONException e) {
            System.out.println(e);
        }

        Message replyMessageModerator = new Message();
        replyMessageModerator.setContentText(feedback);
        replyMessageModerator.setConversation(game.getConversation());
        replyMessageModerator.setIsSent(true);
        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
        Long geminiUserId = optionalUser.get().getId();
        User gemini = userRepository.findById(geminiUserId).get();
        replyMessageModerator.setUser(gemini);
        messageRepository.save(replyMessageModerator);





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