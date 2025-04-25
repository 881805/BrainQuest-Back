package com.project.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.dto.LearningScenarioRequest;
import com.project.demo.logic.entity.learning.LearningOption;
import com.project.demo.logic.entity.learning.LearningRepository;
import com.project.demo.logic.entity.learning.LearningScenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LearningService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private LearningRepository learningRepository;

    public LearningScenario generateScenario(LearningScenarioRequest request) {
        String topic = request.getTopic();
        int step = request.getStep();

        List<LearningScenario> prevSteps = learningRepository.findByTopicOrderByStepNumberAsc(topic);

        StringBuilder prevQuestionsText = new StringBuilder();
        for (LearningScenario scenario : prevSteps) {
            prevQuestionsText.append("- ").append(scenario.getQuestion()).append("\n");
        }

        String lastNarrative = prevSteps.isEmpty() ? "" : prevSteps.get(prevSteps.size() - 1).getNarrative();

        String prompt = String.format("""
            Eres un experto en diseño de experiencias de aprendizaje interactivas. Tu tarea es generar un *nuevo paso* de un escenario educativo progresivo para el tema: "%s".

            Este es el paso #%d de la secuencia. La historia debe tener continuidad narrativa con el paso anterior.

            Paso anterior (para continuar la historia):
            "%s"

            Preguntas ya usadas para evitar repeticiones:
            %s

            Formato de salida (solo JSON, sin texto adicional):
            {
              "narrative": "Una breve historia o contexto del escenario (2 a 3 líneas)",
              "question": "Una pregunta clave basada en la narrativa",
              "options": ["Opción A", "Opción B", "Opción C", "Opción D"],
              "correctAnswer": "Texto exacto de la opción correcta"
            }

            Instrucciones adicionales:
            - No repitas narrativa ni preguntas previas.
            - Mantén la historia conectada y coherente entre pasos.
            - Asegúrate de que la pregunta esté basada directamente en la narrativa.
            - Usa un lenguaje educativo y amigable.
            - NO agregues ningún texto explicativo fuera del JSON.
        """, topic, step, lastNarrative, prevQuestionsText);

        String reply = geminiService.getCompletion(prompt).trim()
                .replaceAll("```", "")
                .replaceAll("(?i)^json\\s*", "")
                .trim();

        if (!reply.startsWith("{")) {
            throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
        }

        LearningScenario scenario = parseScenarioResponse(reply, topic, step);
        learningRepository.save(scenario);
        return scenario;
    }

    public List<LearningScenario> getScenariosByTopic(String topic) {
        return learningRepository.findByTopicOrderByStepNumberAsc(topic);
    }

    public Map<String, Object> processFeedback(Long id, String userAnswer) {
        Optional<LearningScenario> optional = learningRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Escenario no encontrado.");
        }

        LearningScenario scenario = optional.get();
        String correct = scenario.getCorrectAnswer();

        if (userAnswer.equalsIgnoreCase(correct)) {
            scenario.setUserAnswer(userAnswer);
            learningRepository.save(scenario);
            return Map.of("message", "¡Respuesta correcta!");
        }

        for (LearningOption opt : scenario.getOptions()) {
            if (opt.getText().equalsIgnoreCase(userAnswer)) {
                opt.setBlocked(true);
                break;
            }
        }

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
            feedback = geminiService.getCompletion(prompt).trim();
            if (feedback.isBlank()) {
                feedback = "No se pudo generar una explicación válida. Intenta nuevamente.";
            }
        } catch (Exception e) {
            feedback = "No se pudo generar la explicación debido a un error en el servicio.";
        }

        feedback = feedback.replaceAll("```", "").replaceAll("(?i)^json\\s*", "").trim();
        scenario.setFeedback(feedback);
        learningRepository.save(scenario);

        return Map.of(
                "feedback", feedback,
                "blockedOption", userAnswer
        );
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

    public Page<LearningScenario> getScenariosByTopic(String topic, Pageable pageable) {
        return learningRepository.findByTopic(topic, pageable);
    }

}