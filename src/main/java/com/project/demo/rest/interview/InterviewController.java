package com.project.demo.rest.interview;

import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.interview.Interview;
import com.project.demo.logic.entity.interview.InterviewRepository;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GeminiService geminiService;

    @Transactional
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendInterviewMessage(@RequestBody Interview interview, HttpServletRequest request) {
        List<Message> messages = interview.getConversation().getMessages();
        Message latestMessage = null;

        if (messages != null && !messages.isEmpty()) {
            latestMessage = messages.get(messages.size() - 1); // último mensaje enviado por el usuario
            messageRepository.save(latestMessage);
        }

        String prompt = messages.toString() +
                " Eres un entrevistador profesional. Responde como si estuvieras conduciendo una entrevista seria. Limita tu respuesta a 3 oraciones." +
                " No repitas las preguntas anteriores. Mantén el foco en evaluar las habilidades y experiencias del usuario de manera profesional.";

        String reply = geminiService.getCompletion(prompt);

        if (reply.length() > 1000) {
            reply = reply.substring(0, 1000);
        }

        Message replyMessage = new Message();
        replyMessage.setContentText(reply);
        replyMessage.setConversation(interview.getConversation());
        replyMessage.setIsSent(true);
        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
        User gemini = optionalUser.get();
        replyMessage.setUser(gemini);

        messageRepository.save(replyMessage);

        if (interview.getElapsedTurns() >= interview.getMaxTurns()) {
            return finishInterview(interview);
        }

        incrementTurns(interview);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void incrementTurns(Interview interview) {
        interview.setElapsedTurns(interview.getElapsedTurns() + 1);
        interviewRepository.save(interview);
    }

    public ResponseEntity<?> finishInterview(@RequestBody Interview interview) {
        List<Message> messages = interview.getConversation().getMessages();
        String context = messages.toString();

        String prompt = context +
                " Eres un evaluador de entrevistas. Escribe un resumen breve de retroalimentación profesional para el candidato." +
                " Empieza el texto con 'Retroalimentación: ' y limita tu respuesta a 5 oraciones. Sé constructivo pero honesto. Usa español.";

        String reply = geminiService.getCompletion(prompt);

        if (reply.length() > 1000) {
            reply = reply.substring(0, 1000);
        }

        Message feedbackMessage = new Message();
        feedbackMessage.setContentText(reply);
        feedbackMessage.setConversation(interview.getConversation());
        feedbackMessage.setIsSent(true);
        Optional<User> optionalUser = userRepository.findByEmail("gemini.google@gmail.com");
        User gemini = optionalUser.get();
        feedbackMessage.setUser(gemini);

        messageRepository.save(feedbackMessage);

        Optional<Interview> interviewOptional = interviewRepository.findById(interview.getId());
        if (interviewOptional.isPresent()) {
            Interview finishedInterview = interviewOptional.get();
            finishedInterview.setOngoing(false);
            interviewRepository.save(finishedInterview);
            return new ResponseEntity<>(finishedInterview, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}