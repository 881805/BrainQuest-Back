package com.project.demo.rest.trivia;

import com.project.demo.dto.FeedbackResponse;
import com.project.demo.dto.UserAnswerRequest;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.service.GeminiService;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import com.project.demo.logic.entity.http.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.demo.service.TriviaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/trivia")
public class TriviaController {

    @Autowired
    private TriviaRepository triviaQuestionRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private TriviaService triviaService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateTriviaQuestion(@RequestBody TriviaQuestion triviaRequest) {
        try {
            TriviaQuestion question = triviaService.generateTriviaQuestion(triviaRequest);
            return new ResponseEntity<>(question, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar la pregunta de trivia: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(@RequestBody UserAnswerRequest request) {
        List<FeedbackResponse> feedbackList = triviaService.generateFeedback(request);
        return ResponseEntity.ok(feedbackList);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> getAllTriviaQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TriviaQuestion> questionPage = triviaQuestionRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(questionPage.getTotalPages());
        meta.setTotalElements(questionPage.getTotalElements());
        meta.setPageNumber(questionPage.getNumber() + 1);
        meta.setPageSize(questionPage.getSize());

        return new GlobalResponseHandler().handleResponse("Trivia questions retrieved successfully",
                questionPage.getContent(), HttpStatus.OK, meta);
    }

}
