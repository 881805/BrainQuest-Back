package com.project.demo.rest.game;


import com.project.demo.gemini.GeminiService;
import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.config.ConfigRepository;
import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.message.MessageRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/games")
@RestController
public class GameController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private TriviaRepository triviaRepository;

    private AdminSeeder adminSeeder;

    @Autowired
    private GameRepository gameRepository;


    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;

    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGame(@RequestBody Game game, HttpServletRequest request) {
        // Fetch User by winner ID
        User winner = userRepository.findById(game.getWinner().getId()).orElse(null);

        // Fetch Conversation by conversation ID (if exists)
        Conversation conversation = game.getConversation() != null ?
                conversationRepository.findById(game.getConversation().getId())
                        .orElseThrow(() -> new RuntimeException("Conversation not found")) : null;

        // Fetch TriviaQuestion by triviaQuestion ID (if exists)
        TriviaQuestion triviaQuestion = game.getQuestion() != null ?
                triviaRepository.findById(game.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("Trivia Question not found")) : null;

        // Fetch GameType by gameType ID
        GameType gameType = game.getGameType() != null ?
                gameTypeRepository.findById(game.getGameType().getId())
                        .orElseThrow(() -> new RuntimeException("Game Type not found")) : null;

        // Set fetched entities into the game object
        game.setWinner(winner);
        game.setConversation(conversation);
        game.setQuestion(triviaQuestion);
        game.setGameType(gameType);

        // Save the game
        gameRepository.save(game);

        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }



    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Game> gamePage = gameRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(gamePage.getTotalPages());
        meta.setTotalElements(gamePage.getTotalElements());
        meta.setPageNumber(gamePage.getNumber() + 1);
        meta.setPageSize(gamePage.getSize());

        return new GlobalResponseHandler().handleResponse("Games retrieved successfully",
                gamePage.getContent(), HttpStatus.OK, meta);
    }


    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getByUserId(
            @PathVariable Long userId) {
        User user = new User(userId);
        // Pass conversationId and pageable to the repository method
       Optional<Game> foundGame = gameRepository.findByWinnerAndIsOngoingTrue(user);


        return new ResponseEntity<>(foundGame.get(), HttpStatus.CREATED);
    }
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateGame(@RequestBody Game game, HttpServletRequest request) {

        User winner = userRepository.findById(game.getWinner().getId()).orElse(null);

        // Fetch Conversation by conversation ID (if exists)
        Conversation conversation = game.getConversation() != null ?
                conversationRepository.findById(game.getConversation().getId())
                        .orElseThrow(() -> new RuntimeException("Conversation not found")) : null;

        // Fetch TriviaQuestion by triviaQuestion ID (if exists)
        TriviaQuestion triviaQuestion = game.getQuestion() != null ?
                triviaRepository.findById(game.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("Trivia Question not found")) : null;

        // Fetch GameType by gameType ID
        GameType gameType = game.getGameType() != null ?
                gameTypeRepository.findById(game.getGameType().getId())
                        .orElseThrow(() -> new RuntimeException("Game Type not found")) : null;

        Game savedGame = gameRepository.save(game);
        return new GlobalResponseHandler().handleResponse(
                "Game successfully updated",
                savedGame,
                HttpStatus.OK,
                request);
    }

    @DeleteMapping("/{gameId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long gameId, HttpServletRequest request) {
        Optional<Game> foundGame = gameRepository.findById(gameId);
        if(foundGame.isPresent()) {
            gameRepository.delete(foundGame.get());

            return new GlobalResponseHandler().handleResponse("Game deleted successfully",
                    foundGame.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Game id " + gameId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
