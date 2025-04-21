package com.project.demo.logic.entity.gameType;


import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class GameTypeSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final GameTypeRepository gameRepository;


    public GameTypeSeeder(GameTypeRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        GameTypeEnum[] gameTypeNames = new GameTypeEnum[] { GameTypeEnum.TRIVIA, GameTypeEnum.DEBATE, GameTypeEnum.MULTIPLAYER_DEBATE,GameTypeEnum.INTERVIEW,GameTypeEnum.STORYBUILDER, GameTypeEnum.TYPING };
        Map<GameTypeEnum, String> roleDescriptionMap = Map.of(
                GameTypeEnum.TRIVIA, "Triva game mode",
                GameTypeEnum.DEBATE, "Debate game mode",
                GameTypeEnum.MULTIPLAYER_DEBATE, " Multiplayer debate game mode",
                GameTypeEnum.INTERVIEW, "Interview game mode",
                GameTypeEnum.TYPING,"Typing  game mode"
        );

        Arrays.stream(gameTypeNames).forEach((gameType) -> {
            Optional<GameType> optionalGameType = gameRepository.findByGameType(gameType);

            optionalGameType.ifPresentOrElse(System.out::println, () -> {
                GameType gameTypeToCreate = new GameType();

                gameTypeToCreate.setGameType(gameType);
                gameTypeToCreate.setDescription(roleDescriptionMap.get(gameType));

                gameRepository.save(gameTypeToCreate);
            });
        });
    }
}
