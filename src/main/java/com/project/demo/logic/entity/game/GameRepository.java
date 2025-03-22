package com.project.demo.logic.entity.game;

import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByWinnerAndIsOngoingTrue(User winner);
}
