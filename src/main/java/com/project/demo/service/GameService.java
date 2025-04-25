package com.project.demo.service;

import com.project.demo.logic.entity.conversation.Conversation;
import com.project.demo.logic.entity.conversation.ConversationRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.rol.AdminSeeder;
import com.project.demo.logic.entity.trivia.TriviaQuestion;
import com.project.demo.logic.entity.trivia.TriviaRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class GameService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private TriviaRepository triviaRepository;


    @Autowired
    private GameRepository gameRepository;

    public Game createGame(Game game){
        User winner = userRepository.findById(game.getWinner().getId()).orElse(null);

        Conversation conversation = new Conversation();
        conversation.setUser1(winner);
        conversation.setUser2(winner);
        conversation.setMultiplayer(false);
        conversation.setCreateDate(LocalDateTime.now());

        conversationRepository.save(conversation);

        TriviaQuestion triviaQuestion = game.getQuestion() != null ?
                triviaRepository.findById(game.getQuestion().getId())
                        .orElseThrow(() -> new RuntimeException("Trivia Question not found")) : null;

        // Fetch GameType by gameType ID
        GameType gameType = game.getGameType() != null ?
                gameTypeRepository.findById(game.getGameType().getId())
                        .orElseThrow(() -> new RuntimeException("Game Type not found")) : null;

        game.setWinner(winner);
        game.setConversation(conversation);
        game.setQuestion(triviaQuestion);
        game.setGameType(gameType);

        if(game.getPointsEarnedPlayer1()>0){
            winner.setExperience(winner.getExperience()+game.getPointsEarnedPlayer1());
            userRepository.save(winner);
        }

        gameRepository.save(game);

        return game;
    }
}
