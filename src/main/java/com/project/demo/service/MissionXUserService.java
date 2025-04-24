package com.project.demo.service;


import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.missionXUser.MissionXUserRepository;
import com.project.demo.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MissionXUserService {
    @Autowired
    private MissionXUserRepository missionXUserRepository;


    public List<MissionXUser> randomizeMissions(User user, List<Mission> missions, List<MissionXUser> existingMissions, int amountToGenerate) {
        List<MissionXUser> newMissions = new ArrayList<>();

        // Extract IDs of already assigned missions
        Set<Integer> assignedMissionIds = existingMissions.stream()
                .map(mu -> mu.getMission().getId())
                .collect(Collectors.toSet());

        // Shuffle all available missions for randomness
        Collections.shuffle(missions);

        for (Mission mission : missions) {
            if (!assignedMissionIds.contains(mission.getId())) {
                MissionXUser mxu = new MissionXUser(
                        null,
                        user,
                        false,
                        mission,
                        null,
                        new Date(),
                        0
                );
                newMissions.add(mxu);
                assignedMissionIds.add(mission.getId()); // Prevent duplicates in the same session
                missionXUserRepository.save(mxu);
                if (newMissions.size() >= amountToGenerate) break;
            }
        }

        return newMissions;
    }


}
