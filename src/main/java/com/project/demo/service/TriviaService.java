package com.project.demo.service;

import com.project.demo.dto.FeedbackResponse;
import com.project.demo.dto.UserAnswerRequest;
import com.project.demo.logic.entity.trivia.Option;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class TriviaService {

    @Autowired
    private TriviaRepository triviaRepository;

    @Autowired
    private GeminiService geminiService;

    public TriviaQuestion generateTriviaQuestion(TriviaQuestion triviaRequest) {
        List<TriviaQuestion> preguntasPrevias = triviaRepository.findByCategoryAndDifficulty(
                triviaRequest.getCategory(), triviaRequest.getDifficulty());

        StringBuilder preguntasPreviasTexto = new StringBuilder();
        for (TriviaQuestion q : preguntasPrevias) {
            preguntasPreviasTexto.append("- ").append(q.getQuestion()).append("\n");
        }

        String prompt = "Eres un generador de preguntas de trivia. Devuelve ÚNICAMENTE un JSON válido con este formato exacto:\n" +
                "{ \"question\": \"Texto de la pregunta\", \"options\": [\"Opción 1\", \"Opción 2\", \"Opción 3\", \"Opción 4\"], \"correctAnswer\": \"Opción correcta\" }\n" +
                "La pregunta debe ser sobre " + triviaRequest.getCategory() + " con dificultad " + triviaRequest.getDifficulty() + ".\n" +
                "Estas son las preguntas que ya se han hecho. NO repitas ninguna de ellas:\n" + preguntasPreviasTexto +
                "\nNO agregues explicaciones, texto adicional ni comentarios. SOLO responde con un JSON válido.";

        String reply = geminiService.getCompletion(prompt);
        reply = reply.trim().replaceAll("```", "").replaceAll("(?i)^json\\s*", "").trim();

        if (!reply.startsWith("{")) {
            throw new RuntimeException("Respuesta de Gemini no es JSON válido: " + reply);
        }

        TriviaQuestion question = parseResponse(reply, triviaRequest.getCategory(), triviaRequest.getDifficulty());

        Optional<TriviaQuestion> existingQuestion = triviaRepository.findByQuestionAndCategoryAndDifficulty(
                question.getQuestion(), question.getCategory(), question.getDifficulty());

        if (existingQuestion.isPresent()) {
            throw new RuntimeException("Ya existe una pregunta similar en la base de datos.");
        }

        return triviaRepository.save(question);
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
            throw new RuntimeException("Error al parsear la respuesta de Gemini", e);
        }
    }

    public List<FeedbackResponse> generateFeedback(UserAnswerRequest request) {
        List<FeedbackResponse> feedbackList = new ArrayList<>();

        for (UserAnswerRequest.AnswerItem answerItem : request.getAnswers()) {
            Optional<TriviaQuestion> optionalQuestion = triviaRepository.findById(answerItem.getQuestionId());

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
                        question.getQuestion(), userAnswer, correctAnswer);

                String feedback;
                try {
                    feedback = geminiService.getCompletion(prompt);
                } catch (Exception e) {
                    feedback = "No se pudo generar la explicación.";
                }

                feedback = feedback.trim().replaceAll("```", "").replaceAll("(?i)^json\\s*", "").trim();
                question.setFeedback(feedback);
                triviaRepository.save(question);

                feedbackList.add(new FeedbackResponse(
                        question.getId(),
                        question.getQuestion(),
                        userAnswer,
                        correctAnswer,
                        feedback
                ));
            }
        }

        return feedbackList;
    }

}
