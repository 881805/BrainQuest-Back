package com.project.demo.rest.trivia;

import com.project.demo.dto.FeedbackResponse;
import com.project.demo.dto.UserAnswerRequest;
import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.trivia.Option;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            // Obtener preguntas anteriores por categoría y dificultad
            List<TriviaQuestion> preguntasPrevias = triviaQuestionRepository.findByCategoryAndDifficulty(
                    triviaRequest.getCategory(), triviaRequest.getDifficulty());

            // Armar una lista con las preguntas ya existentes
            StringBuilder preguntasPreviasTexto = new StringBuilder();
            for (TriviaQuestion q : preguntasPrevias) {
                preguntasPreviasTexto.append("- ").append(q.getQuestion()).append("\n");
            }

            // Armar el prompt con las preguntas previas incluidas
            String prompt = "Eres un generador de preguntas de trivia. Devuelve ÚNICAMENTE un JSON válido con este formato exacto:\n" +
                    "{ \"question\": \"Texto de la pregunta\", \"options\": [\"Opción 1\", \"Opción 2\", \"Opción 3\", \"Opción 4\"], \"correctAnswer\": \"Opción correcta\" }\n" +
                    "La pregunta debe ser sobre " + triviaRequest.getCategory() + " con dificultad " + triviaRequest.getDifficulty() + ".\n" +
                    "Estas son las preguntas que ya se han hecho. NO repitas ninguna de ellas:\n" + preguntasPreviasTexto +
                    "\nNO agregues explicaciones, texto adicional ni comentarios. SOLO responde con un JSON válido.";

            String reply = geminiService.getCompletion(prompt);
            System.out.println("Respuesta de Gemini: " + reply);

            reply = reply.trim().replaceAll("```", "")
                    .replaceAll("(?i)^json\\s*", "")
                    .trim();

            if (!reply.trim().startsWith("{")) {
                throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
            }

            TriviaQuestion question = parseResponse(reply, triviaRequest.getCategory(), triviaRequest.getDifficulty());

            // Verificar si la pregunta ya existía (medida de seguridad extra)
            Optional<TriviaQuestion> existingQuestion = triviaQuestionRepository.findByQuestionAndCategoryAndDifficulty(
                    question.getQuestion(), question.getCategory(), question.getDifficulty());

            if (existingQuestion.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe una pregunta similar en la base de datos.");
            }

            // Guardar la nueva pregunta
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

            if (!jsonNode.has("question") || !jsonNode.has("options") || !jsonNode.has("correctAnswer")) {
                throw new RuntimeException("Formato incorrecto en la respuesta de Gemini: " + response);
            }

            TriviaQuestion question = new TriviaQuestion();
            question.setQuestion(jsonNode.get("question").asText());

            String correctAnswer = jsonNode.get("correctAnswer").asText();
            List<Option> options = new ArrayList<>();

            jsonNode.get("options").forEach(optionNode -> {
                String text = optionNode.asText();
                boolean isCorrect = text.equalsIgnoreCase(correctAnswer);
                options.add(new Option(text, isCorrect));
            });

            question.setOptions(options);
            question.setCorrectAnswer(correctAnswer);
            question.setCategory(category);
            question.setDifficulty(difficulty);

            return question;
        } catch (Exception e) {
            System.out.println("Error al parsear la respuesta: " + response);
            throw new RuntimeException("Error al parsear la respuesta de Gemini", e);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(@RequestBody UserAnswerRequest request) {
        List<FeedbackResponse> feedbackList = new ArrayList<>();

        for (UserAnswerRequest.AnswerItem answerItem : request.getAnswers()) {
            Optional<TriviaQuestion> optionalQuestion = triviaQuestionRepository.findById(answerItem.getQuestionId());

            if (optionalQuestion.isEmpty()) continue;

            TriviaQuestion question = optionalQuestion.get();
            String userAnswer = answerItem.getUserAnswer();
            String correctAnswer = question.getCorrectAnswer();

            if (!correctAnswer.equalsIgnoreCase(userAnswer)) {
                String prompt = String.format("""
                Pregunta: %s
                Tu respuesta: %s
                Respuesta correcta: %s
                Explica brevemente por qué la respuesta correcta es la adecuada y la del usuario es incorrecta, en máximo 3 líneas. No repitas la pregunta ni los encabezados.
                """,
                        question.getQuestion(), userAnswer, correctAnswer
                );

                String feedback;
                try {
                    feedback = geminiService.getCompletion(prompt);
                } catch (Exception e) {
                    feedback = "No se pudo generar la explicación.";
                }

                feedback = feedback.trim().replaceAll("```", "").replaceAll("(?i)^json\\s*", "").trim();
                question.setFeedback(feedback); // opcional: podrías guardarlo si querés
                triviaQuestionRepository.save(question); // si decidís persistirlo

                feedbackList.add(new FeedbackResponse(
                        question.getId(),
                        question.getQuestion(),
                        userAnswer,
                        correctAnswer,
                        feedback
                ));
            }
        }

        return ResponseEntity.ok(feedbackList);
    }
}
