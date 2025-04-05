package com.project.demo.logic.entity.game;

import com.project.demo.logic.entity.message.Message;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByWinnerAndIsOngoingTrue(User winner);
    Page<Game> findByWinnerAndIsOngoingTrue(User user, Pageable pageable);

    @Query("SELECT g FROM Game g JOIN g.conversation c WHERE c.user1 = :user AND g.isOngoing = true")
    Page<Game> findByConversationUser1AndIsOngoingTrue(@Param("user") User user, Pageable pageable);

}
