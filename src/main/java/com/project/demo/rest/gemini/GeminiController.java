package com.project.demo.rest.gemini;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/gemini")
@RestController
public class
GeminiController {

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


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> judgeDebate(@RequestBody Conversation conversation, HttpServletRequest request){

        Optional<Conversation> conversationOptional = conversationRepository.findById(conversation.getId());

        List<Message> messages = conversationOptional.get().getMessages();
        Optional<Config> userConfig = configRepository.findByUser(messages.get(0).getUser());

        String conversationString = conversationOptional.get().toString();

        System.out.println(conversationString);

        String reply= geminiService.getCompletion(conversationString+" Send a json reply with the following format {feedback: '', score: 0} " +
                "Where the score is obtained by judging the performance of user1 and award them an ammount of points from 0 to 500. Please keep your reply short sending only the number and a small sentence for feedback inside the json."); //respuesta de ia genera

        reply = trimJson(reply);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
}


public String trimJson(String json) {
    Pattern pattern = Pattern.compile("\\{.*}", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(json);

    if (matcher.find()) {
        json = matcher.group(0);
       return json;
    } else {
        System.out.println("No JSON found.");
        return "{}";
    }
}
}
