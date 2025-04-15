package com.project.demo.logic.entity.mission;


import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    Page<Mission> findById(long missionId, Pageable pageable);

    List<Mission> findByIsActiveTrue();

    List<Mission> findByEndDateBeforeAndIsActiveTrue(LocalDate now);
}
