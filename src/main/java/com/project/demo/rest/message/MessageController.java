package com.project.demo.rest.message;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
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
    @Autowired
    private ConfigRepository configRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request) {
        messageRepository.save(message); //guarda mensaje enviado por el usuario

        Optional<Config> userConfig = configRepository.findByUser(message.getUser());

        String reply= geminiService.getCompletion(message.getContentText()); //respuesta de ia genera

        //salvando la respuesta de la IA
        Message replyMessage = new Message();
        replyMessage.setContentText(reply);

        replyMessage.setConversation(message.getConversation());


        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
       Long geminiUserId = optionalUser.get().getId();



        User gemini = userRepository.findById(geminiUserId).get();

        replyMessage.setUser(gemini);


        messageRepository.save(replyMessage);

        return new ResponseEntity<>(replyMessage, HttpStatus.CREATED);
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Message> messagePage = messageRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(messagePage.getTotalPages());
        meta.setTotalElements(messagePage.getTotalElements());
        meta.setPageNumber(messagePage.getNumber() + 1);
        meta.setPageSize(messagePage.getSize());

        return new GlobalResponseHandler().handleResponse("Messages retrieved successfully",
                messagePage.getContent(), HttpStatus.OK, meta);
    }


    @GetMapping("/{conversationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            @PathVariable Long conversationId) {

        Pageable pageable = PageRequest.of(page - 1, size);

        // Pass conversationId and pageable to the repository method
        Page<Message> messagePage = messageRepository.findByConversationId(conversationId, pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(messagePage.getTotalPages());
        meta.setTotalElements(messagePage.getTotalElements());
        meta.setPageNumber(messagePage.getNumber() + 1);
        meta.setPageSize(messagePage.getSize());

        return new GlobalResponseHandler().handleResponse("Messages retrieved successfully",
                messagePage.getContent(), HttpStatus.OK, meta);
    }

    @PutMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMessage(@PathVariable Long messageId, @RequestBody Message message, HttpServletRequest request) {
        Optional<Message> foundMessage = messageRepository.findById(messageId);

        if (!foundMessage.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Message ID " + messageId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }

        Optional<User> foundUser = userRepository.findById(message.getUser().getId());

        if (!foundUser.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Message ID " + messageId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }


        Message existingMessage = foundMessage.get();

        // Update fields only if they are provided in the request
        if (message.getContentText() != null) {
            existingMessage.setContentText(message.getContentText());
        }
        if (message.getConversation() != null) {
            existingMessage.setConversation(message.getConversation());
        }

        if (message.getUser() != null) {
            existingMessage.setUser(foundUser.get());
        }
        // Handle Category Update


        // Save updated Producto
        Message updatedMessage = messageRepository.save(existingMessage);
        return new GlobalResponseHandler().handleResponse("Message updated successfully",
                updatedMessage, HttpStatus.OK, request);
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId, HttpServletRequest request) {
        Optional<Message> foundMessage = messageRepository.findById(messageId);
        if(foundMessage.isPresent()) {
            messageRepository.delete(foundMessage.get());

            return new GlobalResponseHandler().handleResponse("Message deleted successfully",
                    foundMessage.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Message id " + messageId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
