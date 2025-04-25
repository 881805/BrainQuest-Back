package com.project.demo.service;


import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.missionXUser.MissionXUserRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MissionXUserService {
    @Autowired
    private MissionXUserRepository missionXUserRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> updateMission(MissionXUser mission, long missionId, HttpServletRequest request ) {
        Optional<MissionXUser> foundMission = missionXUserRepository.findById(missionId);

        if (!foundMission.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Mission ID " + missionId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }

        MissionXUser updatedMission = foundMission.get();
        updatedMission.setIsCompleted(mission.getIsCompleted());
        updatedMission.setCompletedAt(mission.getCompletedAt());
        updatedMission.setLastUpdated(new Date());
        updatedMission.setProgress(mission.getProgress());

        //guarda la experiencia del usuario en caso de una mision ser completada
        if(mission.getProgress()>=foundMission.get().getMission().getObjective().getAmmountSuccesses() && foundMission.get().getMission().getIsActive()==true) {
            User foundUser = userRepository.getById(updatedMission.getUser().getId());

            foundUser.setExperience(foundUser.getExperience()+foundMission.get().getMission().getExperience());
            userRepository.save(foundUser);
            updatedMission.setIsCompleted(true);
            missionXUserRepository.save(updatedMission);
            return new GlobalResponseHandler().handleResponse("Mision completada con exito: " + updatedMission.getMission().getObjective().getObjectiveText(),
                    updatedMission, HttpStatus.OK, request);
        }
        missionXUserRepository.save(updatedMission);

        return new GlobalResponseHandler().handleResponse("Mision actualizada con exito: " + updatedMission.getMission().getObjective().getObjectiveText(),
                updatedMission, HttpStatus.OK, request);


    }
    public List<MissionXUser> randomizeMissions(User user, List<Mission> missions, List<MissionXUser> existingMissions, int amountToGenerate) {
        List<MissionXUser> newMissions = new ArrayList<>();


        Set<Integer> assignedMissionIds = existingMissions.stream()
                .map(mu -> mu.getMission().getId())
                .collect(Collectors.toSet());

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
