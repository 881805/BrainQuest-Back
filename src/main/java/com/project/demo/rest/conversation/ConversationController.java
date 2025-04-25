package com.project.demo.rest.conversation;


import com.project.demo.service.GeminiService;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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

@RequestMapping("/conversations")
@RestController
public class ConversationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GeminiService geminiService;

    private AdminSeeder adminSeeder;

    public ConversationController(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
        this.geminiService = geminiService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createConversation(@RequestBody Conversation conversation, HttpServletRequest request) {

        User user1 = userRepository.findById(conversation.getUser1().getId()).orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(conversation.getUser2().getId()).orElse(null);


        conversation.setUser1(user1);
        conversation.setUser2(user2);

        conversationRepository.save(conversation);

        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllbyUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            @PathVariable Long userId) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Conversation> messagePage = conversationRepository.findByUserId(userId,pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(messagePage.getTotalPages());
        meta.setTotalElements(messagePage.getTotalElements());
        meta.setPageNumber(messagePage.getNumber() + 1);
        meta.setPageSize(messagePage.getSize());

        return new GlobalResponseHandler().handleResponse("Conversations retrieved successfully",
                messagePage.getContent(), HttpStatus.OK, meta);
    }




    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);


        Page<Conversation> messagePage = conversationRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(messagePage.getTotalPages());
        meta.setTotalElements(messagePage.getTotalElements());
        meta.setPageNumber(messagePage.getNumber() + 1);
        meta.setPageSize(messagePage.getSize());

        return new GlobalResponseHandler().handleResponse("Messages retrieved successfully",
                messagePage.getContent(), HttpStatus.OK, meta);
    }

    @DeleteMapping("/{conversationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteConversation(@PathVariable Long conversationId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);

        if (conversationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Conversation not found.");
        }

        conversationRepository.delete(conversationOptional.get()); // ✅ Deletes messages too

        return ResponseEntity.status(HttpStatus.OK)
                .body("Conversation and related messages deleted successfully.");
    }



}
