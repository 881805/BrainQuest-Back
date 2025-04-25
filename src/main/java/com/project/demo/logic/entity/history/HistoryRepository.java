package com.project.demo.logic.entity.history;

import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeEnum;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findAllByUser(User user, Pageable pageable);

    Page<History> findByUserAndGame_GameType_GameTypeAndGame_IsOngoingTrue(
            User user,
            GameTypeEnum gameType,
            Pageable pageable
    );


}
