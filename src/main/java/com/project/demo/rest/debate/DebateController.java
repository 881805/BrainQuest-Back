package com.project.demo.rest.debate;



import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.service.DebateService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/debates")
@RestController
public class DebateController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;



    private AdminSeeder adminSeeder;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GameRepository gameRepository;


    @Autowired
    private DebateService debateService;
    @Autowired
    private AiConfigurationRepository aiConfigurationRepository;

    public DebateController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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