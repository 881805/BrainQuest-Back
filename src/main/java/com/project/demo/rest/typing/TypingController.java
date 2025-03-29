package com.project.demo.rest.typing;

import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.typing.Typing;
import com.project.demo.logic.entity.typing.TypingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/typing")
public class TypingController {

    @Autowired
    private TypingRepository typingRepository;

    @Autowired
    private GeminiService geminiService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateTypingExercise(@RequestBody Typing typingRequest) {
        try {
            // 🔹 Nueva versión del prompt para asegurar JSON válido
            String prompt = "Eres un generador de ejercicios de mecanografía. Devuelve ÚNICAMENTE un JSON válido con este formato exacto:\n" +
                    "{ \"text\": \"Texto de máximo 200 caracteres\", \"timeLimit\": 60, \"hints\": [\"Pista 1\", \"Pista 2\"] }\n" +
                    "El ejercicio debe ser sobre " + typingRequest.getCategory() + " con dificultad " + typingRequest.getDifficulty() +
                    ". NO agregues explicaciones, texto adicional ni comentarios, SOLO el JSON.";


            // Llamar a Gemini y obtener la respuesta
            String reply = geminiService.getCompletion(prompt);
            System.out.println("Respuesta de Gemini: " + reply); // Debugging

            // 🔹 Limpiar la respuesta
            reply = reply.trim().replaceAll("```", "")
                    .replaceAll("(?i)^json\\s*", "")  // Eliminar "json" al principio
                    .trim();

            // 🔹 Validar si la respuesta es JSON
            if (!reply.startsWith("{")) {
                throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
            }

            // Convertir la respuesta de Gemini a un objeto Typing
            Typing typingExercise = parseResponse(reply, typingRequest.getCategory(), typingRequest.getDifficulty());

            // Guardar en la base de datos
            typingRepository.save(typingExercise);

            return new ResponseEntity<>(typingExercise, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el ejercicio de escritura: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Typing>> getAllTypingExercises() {
        return ResponseEntity.ok(typingRepository.findAll());
    }

    private Typing parseResponse(String response, String category, String difficulty) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // 🔹 Validar si falta algún campo
            if (!jsonNode.has("text") || !jsonNode.has("timeLimit") || !jsonNode.has("hints")) {
                throw new RuntimeException("Formato incorrecto en la respuesta de Gemini: " + response);
            }

            // Crear el ejercicio de escritura
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
            System.out.println("Error al parsear la respuesta: " + response);
            throw new RuntimeException("Error al parsear la respuesta de Gemini", e);
        }
    }
}
