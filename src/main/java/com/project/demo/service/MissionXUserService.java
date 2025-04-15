package com.project.demo.service;


import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MissionXUserService {

    public MissionXUser randomizeMissions(User user, List<Mission> missions){
        Collections.shuffle(missions);
        Mission mission = missions.get(0);
        MissionXUser missionXUser =  new MissionXUser(
                null,
                user,
                false,
                mission,
                null,
                new Date(),
                0
        );
        return missionXUser;
    }
}
