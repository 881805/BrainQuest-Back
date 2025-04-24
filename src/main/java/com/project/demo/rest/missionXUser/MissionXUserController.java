package com.project.demo.rest.missionXUser;


import com.google.api.client.util.DateTime;
import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.Objective.ObjectiveRepository;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.mission.MissionRepository;
import com.project.demo.logic.entity.missionXUser.MissionXUser;
import com.project.demo.logic.entity.missionXUser.MissionXUserRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.service.MissionXUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/missionsxusers")
@RestController
public class MissionXUserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionXUserRepository missionXUserRepository;

    @Autowired
    private ObjectiveRepository objectiveRepository;


    @Autowired
    private GameTypeRepository gameTypeRepository;


    @Autowired
    private MissionXUserService missionXUserService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMission(@RequestBody MissionXUser missionXUser, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findById(missionXUser.getUser().getId());
        Optional<Mission> optionalMission = missionRepository.findById(Long.valueOf(missionXUser.getMission().getId()));
        missionXUser.setUser(optionalUser.get());
        missionXUser.setMission(optionalMission.get());

        missionXUserRepository.save(missionXUser);

        return new ResponseEntity<>(missionXUser, HttpStatus.CREATED);
    }


    @PostMapping("/assign/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMission(@PathVariable Long userId, HttpServletRequest request) {
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MissionXUser> missionPage = missionXUserRepository.findByUserIdAndActiveMissionAndIsCompletedIsFalse(userId, pageable);
        ArrayList<MissionXUser> addedMissions = null;
        if (missionPage.getContent().size() < 4) {
            int loopAmmount = 4 - missionPage.getContent().size();
            if (loopAmmount <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User user = userRepository.findById(userId).get();
            List<Mission> missionList = missionRepository.findByIsActiveTrue();


            addedMissions = (ArrayList<MissionXUser>) missionXUserService.randomizeMissions(user, missionList, missionPage.getContent(), loopAmmount);


        }
        return new ResponseEntity<>(addedMissions, HttpStatus.CREATED);
    }


    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllByUserId(@PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);

        // Pass conversationId and pageable to the repository method
        Page<MissionXUser> missionPage = missionXUserRepository.findByUserIdAndActiveMissionAndIsCompletedIsFalse(userId, pageable);


        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(missionPage.getTotalPages());
        meta.setTotalElements(missionPage.getTotalElements());
        meta.setPageNumber(missionPage.getNumber() + 1);
        meta.setPageSize(missionPage.getSize());

        return new GlobalResponseHandler().handleResponse("Missions retrieved successfully",
                missionPage.getContent(), HttpStatus.OK, meta);
    }

    @PutMapping("/{missionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateMessage(@PathVariable Long missionId, @RequestBody MissionXUser mission, HttpServletRequest request) {
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


        return new GlobalResponseHandler().handleResponse("Progreso de mision actualizado con exito",
                updatedMission, HttpStatus.OK, request);
    }

    @DeleteMapping("/{missionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long missionId, HttpServletRequest request) {
        Optional<MissionXUser> foundMission = missionXUserRepository.findById(missionId);
        if(foundMission.isPresent()) {
            missionXUserRepository.delete(foundMission.get());

            return new GlobalResponseHandler().handleResponse("Mission deleted successfully",
                    foundMission.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Mission id " + missionId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
