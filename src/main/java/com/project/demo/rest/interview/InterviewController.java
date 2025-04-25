package com.project.demo.rest.interview;


import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.service.GeminiService;
import com.project.demo.service.InterviewService;
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
    private InterviewService interviewService;

    @Transactional
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> simulateInterview(@RequestBody Game game, HttpServletRequest request) {
        try {
            return interviewService.simulateInterview(game, request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en simulación de entrevista: " + e.getMessage());
        }
    }

    @PostMapping("/feedback")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateInterviewFeedback(@RequestBody Game game, HttpServletRequest request) {
        try {
            return interviewService.generateInterviewFeedback(game, request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar feedback: " + e.getMessage());
        }
    }

}