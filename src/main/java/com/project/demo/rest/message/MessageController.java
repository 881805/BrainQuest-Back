package com.project.demo.rest.message;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/messages")
@RestController
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GeminiService geminiService;

    private AdminSeeder adminSeeder;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request) {
        messageRepository.save(message); //guarda mensaje enviado por el usuario

        String reply= geminiService.getCompletion(message.getContentText()); //respuesta de ia genera

        //salvando la respuesta de la IA
        Message replyMessage = new Message();
        replyMessage.setContentText(reply);

        replyMessage.setConversationId(message.getConversationId());


        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
       Long geminiUserId = optionalUser.get().getId();

        replyMessage.setSendingUserId(geminiUserId);


        messageRepository.save(replyMessage);

        return new ResponseEntity<>(replyMessage, HttpStatus.CREATED);
    }
}
