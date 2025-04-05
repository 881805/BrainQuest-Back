package com.project.demo.logic.entity.gameType;

import com.project.demo.logic.entity.rol.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GameTypeRepository extends JpaRepository<GameType, Long> {

    Optional<GameType> findByGameType(GameTypeEnum gameType);
}
