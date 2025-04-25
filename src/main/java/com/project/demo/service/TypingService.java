package com.project.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.typing.Typing;
import com.project.demo.logic.entity.typing.TypingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TypingService {

    @Autowired
    private TypingRepository typingRepository;

    @Autowired
    private GeminiService geminiService;

    public Typing generateTypingExercise(String category, String difficulty) {
        try {
            String prompt = "Eres un generador de ejercicios de mecanografía. Devuelve ÚNICAMENTE un JSON válido con este formato exacto:\n" +
                    "{ \"text\": \"Texto de máximo 200 caracteres\", \"timeLimit\": 60, \"hints\": [\"Pista 1\", \"Pista 2\"] }\n" +
                    "El ejercicio debe ser sobre " + category + " con dificultad " + difficulty +
                    ". NO agregues explicaciones, texto adicional ni comentarios, SOLO el JSON.";

            String reply = geminiService.getCompletion(prompt).trim()
                    .replaceAll("```", "")
                    .replaceAll("(?i)^json\\s*", "")
                    .trim();

            if (!reply.startsWith("{")) {
                throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
            }

            Typing typingExercise = parseResponse(reply, category, difficulty);
            return typingRepository.save(typingExercise);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el ejercicio de escritura: " + e.getMessage(), e);
        }
    }

    public List<Typing> getAllTypingExercises() {
        return typingRepository.findAll();
    }

    private Typing parseResponse(String response, String category, String difficulty) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            if (!jsonNode.has("text") || !jsonNode.has("timeLimit") || !jsonNode.has("hints")) {
                throw new RuntimeException("Formato incorrecto en la respuesta de Gemini: " + response);
            }

            Typing typingExercise = new Typing();
            typingExercise.setText(jsonNode.get("text").asText());
            typingExercise.setTimeLimit(jsonNode.get("timeLimit").asInt());

            List<String> hints = new ArrayList<>();
            jsonNode.get("hints").forEach(hint -> hints.add(hint.asText()));
            typingExercise.setHints(hints);

            typingExercise.setCategory(category);
            typingExercise.setDifficulty(difficulty);

            return typingExercise;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de Gemini", e);
        }
    }
    public Page<Typing> getAllTypingExercises(Pageable pageable) {
        return typingRepository.findAll(pageable);
    }

}