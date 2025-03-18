package com.project.demo.rest.trivia;

import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

@RestController
@RequestMapping("/trivia")
public class TriviaController {

    @Autowired
    private TriviaRepository triviaQuestionRepository;

    @Autowired
    private GeminiService geminiService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateTriviaQuestion(@RequestBody TriviaQuestion triviaRequest) {
        try {
            // 🔹 Nueva versión del prompt asegurando que Gemini devuelva solo JSON válido
            String prompt = "Eres un generador de preguntas de trivia. Devuelve ÚNICAMENTE un JSON válido con este formato exacto:\n" +
                    "{ \"question\": \"Texto de la pregunta\", \"options\": [\"Opción 1\", \"Opción 2\", \"Opción 3\", \"Opción 4\"], \"correctAnswer\": \"Opción correcta\" }\n" +
                    "La pregunta debe ser sobre " + triviaRequest.getCategory() + " con dificultad " + triviaRequest.getDifficulty() +
                    ". NO agregues explicaciones, texto adicional ni comentarios, SOLO el JSON. Debe empezar y terminar el request con los brackets del json para poder pasearse";

            // Llamar a Gemini y obtener la respuesta
            String reply = geminiService.getCompletion(prompt);
            System.out.println("Respuesta de Gemini: " + reply); // Debugging

            // 🔹 Limpiar la respuesta para eliminar la palabra 'json' y los backticks
            reply = reply.trim().replaceAll("```", "")
                    .replaceAll("(?i)^json\\s*", "")  // Eliminar "json" al principio, sin importar mayúsculas/minúsculas
                         // Eliminar los backticks
                    .trim();

            // 🔹 Validar si Gemini devolvió texto en vez de JSON
            if (!reply.trim().startsWith("{")) {
                throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
            }

            // Convertir la respuesta de Gemini a un objeto TriviaQuestion
            TriviaQuestion question = parseResponse(reply, triviaRequest.getCategory(), triviaRequest.getDifficulty());

            // Guardar en la base de datos
            triviaQuestionRepository.save(question);

            return new ResponseEntity<>(question, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar la pregunta de trivia: " + e.getMessage());
        }
    }




    @GetMapping("/all")
    public ResponseEntity<List<TriviaQuestion>> getAllTriviaQuestions() {
        return ResponseEntity.ok(triviaQuestionRepository.findAll());
    }

    private TriviaQuestion parseResponse(String response, String category, String difficulty) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // 🔹 Validar si falta algún campo en el JSON
            if (!jsonNode.has("question") || !jsonNode.has("options") || !jsonNode.has("correctAnswer")) {
                throw new RuntimeException("Formato incorrecto en la respuesta de Gemini: " + response);
            }

            // Crear la pregunta de trivia
            TriviaQuestion question = new TriviaQuestion();
            question.setQuestion(jsonNode.get("question").asText());

            List<String> options = new ArrayList<>();
            jsonNode.get("options").forEach(option -> options.add(option.asText()));
            question.setOptions(options);

            question.setCorrectAnswer(jsonNode.get("correctAnswer").asText());
            question.setCategory(category);
            question.setDifficulty(difficulty);

            return question;
        } catch (Exception e) {
            System.out.println("Error al parsear la respuesta: " + response);
            throw new RuntimeException("Error al parsear la respuesta de Gemini", e);
        }
    }
}
