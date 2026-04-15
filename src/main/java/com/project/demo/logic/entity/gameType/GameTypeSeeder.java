package com.project.demo.logic.entity.gameType;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class GameTypeSeeder implements ApplicationRunner {

    private final GameTypeRepository gameRepository;

    public GameTypeSeeder(GameTypeRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        loadGameTypes();
    }

    private void loadGameTypes() {
        GameTypeEnum[] gameTypes = new GameTypeEnum[] {
                GameTypeEnum.TRIVIA,
                GameTypeEnum.DEBATE,
                GameTypeEnum.MULTIPLAYER_DEBATE,
                GameTypeEnum.INTERVIEW,
                GameTypeEnum.TYPING
        };

        Map<GameTypeEnum, String> descriptions = Map.of(
                GameTypeEnum.TRIVIA, "Trivia game mode",
                GameTypeEnum.DEBATE, "Debate game mode",
                GameTypeEnum.MULTIPLAYER_DEBATE, "Multiplayer debate game mode",
                GameTypeEnum.INTERVIEW, "Interview game mode",
                GameTypeEnum.TYPING, "Typing game mode"
        );

        Arrays.stream(gameTypes).forEach(gameType -> {
            Optional<GameType> existing = gameRepository.findByGameType(gameType);

            if (existing.isPresent()) {
                System.out.println("GameType already exists: " + gameType);
            } else {
                GameType newGameType = new GameType();
                newGameType.setGameType(gameType);
                newGameType.setDescription(descriptions.get(gameType));

                gameRepository.save(newGameType);
                System.out.println("Created GameType: " + gameType);
            }
        });
    }
}