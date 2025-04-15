package com.project.demo.logic.entity.missionXUser;

import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.demo.logic.entity.mission.Mission;

@Repository
public interface MissionXUserRepository  extends JpaRepository<MissionXUser, Long> {


    @Query("SELECT mxu FROM MissionXUser mxu WHERE mxu.user.id = :userId AND mxu.mission.isActive = true")
    Page<MissionXUser> findByUserIdAndActiveMission(Long userId, Pageable pageable);

}
