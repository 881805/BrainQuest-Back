package com.project.demo.rest.learning;

import com.project.demo.dto.LearningScenarioRequest;
import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.learning.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
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
    private LearningRepository learningRepository;

    @Autowired
    private GeminiService geminiService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/generate")
    public ResponseEntity<?> generateScenario(@RequestBody LearningScenarioRequest request) {
        String topic = request.getTopic();
        int step = request.getStep();
        try {
            // Obtener pasos previos para evitar repeticiones
            List<LearningScenario> prevSteps = learningRepository.findByTopicOrderByStepNumberAsc(topic);

            StringBuilder prevQuestionsText = new StringBuilder();
            for (LearningScenario scenario : prevSteps) {
                prevQuestionsText.append("- ").append(scenario.getQuestion()).append("\n");
            }

            String prompt = String.format("""
        Eres un experto en diseño de experiencias de aprendizaje interactivas. Tu tarea es generar un escenario educativo para el tema: "%s".

        Formato de salida (solo JSON, sin texto adicional):
        {
          "narrative": "Una breve historia o contexto del escenario (2 a 3 líneas)",
          "question": "Una pregunta clave basada en la narrativa",
          "options": ["Opción A", "Opción B", "Opción C", "Opción D"],
          "correctAnswer": "Texto exacto de la opción correcta"
        }

        Este es el paso #%d del aprendizaje progresivo. Evita repetir las siguientes preguntas ya usadas:
        %s

        Instrucciones adicionales:
        - No devuelvas explicaciones, solo el JSON.
        - Las opciones deben ser creíbles.
        - Asegúrate de que la pregunta se relacione con la narrativa.
        - Usa un tono educativo, claro y sin tecnicismos innecesarios.
        """, topic, step, prevQuestionsText);

            String reply = geminiService.getCompletion(prompt).trim()
                    .replaceAll("```", "")
                    .replaceAll("(?i)^json\\s*", "")
                    .trim();

            if (!reply.startsWith("{")) {
                throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
            }

            LearningScenario scenario = parseScenarioResponse(reply, topic, step);

            // Guardar nuevo escenario generado
            learningRepository.save(scenario);

            return new ResponseEntity<>(scenario, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando escenario: " + e.getMessage());
        }
    }


    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<LearningScenario>> getScenariosByTopic(@PathVariable String topic) {
        return ResponseEntity.ok(learningRepository.findByTopicOrderByStepNumberAsc(topic));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable Long id, @RequestParam String userAnswer) {
        Optional<LearningScenario> optional = learningRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Escenario no encontrado.");
        }

        LearningScenario scenario = optional.get();
        String correct = scenario.getCorrectAnswer();

        // Si la respuesta del usuario es correcta
        if (userAnswer.equalsIgnoreCase(correct)) {
            scenario.setUserAnswer(userAnswer);
            learningRepository.save(scenario);
            return ResponseEntity.ok("¡Respuesta correcta!");
        }

        // Marcar la opción incorrecta como bloqueada
        for (LearningOption opt : scenario.getOptions()) {
            if (opt.getText().equalsIgnoreCase(userAnswer)) {
                opt.setBlocked(true);
                break;
            }
        }

        // Crear el prompt para Gemini
        String prompt = String.format("""
    Eres un tutor educativo. El estudiante eligió una opción incorrecta en una pregunta de aprendizaje progresivo.

    Escenario: %s
    Pregunta: %s
    Tu respuesta: %s
    Respuesta correcta: %s

    Explica brevemente por qué la respuesta correcta es la adecuada y la del usuario es incorrecta (máx. 3 líneas).
    Responde solo con el texto explicativo, sin encabezados ni enumeraciones.
    """, scenario.getNarrative(), scenario.getQuestion(), userAnswer, correct);

        String feedback;
        try {
            // Llamada a Gemini para obtener el feedback
            feedback = geminiService.getCompletion(prompt).trim();

            // Validación del feedback
            if (feedback.isBlank()) {
                feedback = "No se pudo generar una explicación válida. Intenta nuevamente.";
            }

        } catch (Exception e) {
            // En caso de error, devolvemos un mensaje genérico
            feedback = "No se pudo generar la explicación debido a un error en el servicio.";
        }

        // Limpiar cualquier formato extraño
        feedback = feedback.replaceAll("```", "").replaceAll("(?i)^json\\s*", "").trim();

        // Guardar el feedback en el escenario
        scenario.setFeedback(feedback);
        learningRepository.save(scenario);

        // Devolver la respuesta con el feedback y la opción bloqueada
        Map<String, Object> response = new HashMap<>();
        response.put("feedback", feedback);
        response.put("blockedOption", userAnswer);

        return ResponseEntity.ok(response);
    }


    private LearningScenario parseScenarioResponse(String response, String topic, Integer step) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);

            LearningScenario scenario = new LearningScenario();
            scenario.setNarrative(node.get("narrative").asText());
            scenario.setQuestion(node.get("question").asText());
            scenario.setCorrectAnswer(node.get("correctAnswer").asText());
            scenario.setTopic(topic);
            scenario.setStepNumber(step);

            List<LearningOption> options = new ArrayList<>();
            node.get("options").forEach(optNode -> {
                String text = optNode.asText();
                boolean isCorrect = text.equalsIgnoreCase(scenario.getCorrectAnswer());
                options.add(new LearningOption(text, isCorrect, false));
            });
            scenario.setOptions(options);

            return scenario;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear JSON de escenario", e);
        }
    }
}