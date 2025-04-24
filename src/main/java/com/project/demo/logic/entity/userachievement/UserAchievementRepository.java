package com.project.demo.logic.entity.userachievement;

import com.project.demo.logic.entity.achievement.Achievement;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    @Query("SELECT mxu FROM UserAchievement mxu WHERE mxu.user.id = :userId")
    Page<UserAchievement> findByUserId(@Param("userId") Long userId, Pageable pageable);
}