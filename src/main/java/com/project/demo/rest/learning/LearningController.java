package com.project.demo.rest.learning;

import com.project.demo.dto.LearningScenarioRequest;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.learning.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.demo.service.GeminiService;
import com.project.demo.service.LearningService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import com.project.demo.logic.entity.learning.LearningOption;
import com.project.demo.logic.entity.learning.LearningScenario;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/learning")
public class LearningController {

    @Autowired
    private LearningService learningService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateScenario(@RequestBody LearningScenarioRequest request) {
        try {
            LearningScenario scenario = learningService.generateScenario(request);
            return new ResponseEntity<>(scenario, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando escenario: " + e.getMessage());
        }
    }

    @GetMapping("/topic/{topic}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getScenariosByTopic(
            @PathVariable String topic,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LearningScenario> scenarioPage = learningService.getScenariosByTopic(topic, pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(scenarioPage.getTotalPages());
        meta.setTotalElements(scenarioPage.getTotalElements());
        meta.setPageNumber(scenarioPage.getNumber() + 1);
        meta.setPageSize(scenarioPage.getSize());

        return new GlobalResponseHandler().handleResponse("Learning scenarios retrieved successfully",
                scenarioPage.getContent(), HttpStatus.OK, meta);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable Long id, @RequestParam String userAnswer) {
        try {
            Map<String, Object> feedbackResult = learningService.processFeedback(id, userAnswer);
            return ResponseEntity.ok(feedbackResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar feedback: " + e.getMessage());
        }
    }
}