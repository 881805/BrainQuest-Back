package com.project.demo.rest.debate;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.service.DebateService;
import jakarta.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/debates")
@RestController
public class DebateController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GeminiService geminiService;


    private AdminSeeder adminSeeder;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConversationRepository conversationRepository;


    @Autowired
    private GameRepository gameRepository;


    @Autowired
    private DebateService debateService;

    public DebateController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
    }

    @Transactional
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> messageSent(@RequestBody Game game, HttpServletRequest request) {

        ResponseEntity<?> response = null;
        try{
            response = debateService.handleMessages(game);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(response == null){
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
       return response;

    }
}