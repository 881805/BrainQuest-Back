package com.project.demo.scheduler;

import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.mission.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MissionScheduler {

    private final MissionRepository missionRepository;

    public MissionScheduler(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    // This will run every 5 minutes (300,000 milliseconds)
    @Scheduled(fixedRate = 300000)
    public void updateExpiredMissions() {
        List<Mission> expiredMissions = missionRepository.findByEndDateBeforeAndIsActiveTrue(LocalDate.now());

        for (Mission mission : expiredMissions) {
            mission.setIsActive(false);  // Set mission as inactive
            missionRepository.save(mission);  // Save updated mission
        }
    }
}
