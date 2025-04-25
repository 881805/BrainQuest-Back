package com.project.demo.rest.message;


import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.Objective.ObjectiveRepository;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.mission.Mission;
import com.project.demo.logic.entity.mission.MissionRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/missions")
@RestController
public class MissionController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ObjectiveRepository objectiveRepository;


    @Autowired
    private GameTypeRepository gameTypeRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createMission(@RequestBody Mission mission, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findById(mission.getCreatedBy().getId());
        Optional<GameType> optionalGameType = gameTypeRepository.findById(mission.getGameType().getId());
        mission.setCreatedBy(optionalUser.get());
        mission.setGameType(optionalGameType.get());

        objectiveRepository.save(mission.getObjective());
        missionRepository.save(mission);

        return new ResponseEntity<>(mission, HttpStatus.CREATED);
    }




    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Mission> missionPage = missionRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(missionPage.getTotalPages());
        meta.setTotalElements(missionPage.getTotalElements());
        meta.setPageNumber(missionPage.getNumber() + 1);
        meta.setPageSize(missionPage.getSize());

        return new GlobalResponseHandler().handleResponse("Misiones coneseguidas con éxito",
                missionPage.getContent(), HttpStatus.OK, meta);
    }


    @GetMapping("/{missionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            @PathVariable long missionId) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Mission> missionPage = missionRepository.findById(missionId, pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(missionPage.getTotalPages());
        meta.setTotalElements(missionPage.getTotalElements());
        meta.setPageNumber(missionPage.getNumber() + 1);
        meta.setPageSize(missionPage.getSize());

        return new GlobalResponseHandler().handleResponse("Misiones coneseguidas con éxito",
                missionPage.getContent(), HttpStatus.OK, meta);
    }

    @PutMapping("/{missionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateMessage(@PathVariable Long missionId, @RequestBody Mission mission, HttpServletRequest request) {
        Optional<Mission> foundMission = missionRepository.findById(missionId);

        if (!foundMission.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Mision id " + missionId + " no encontrado" ,
                    HttpStatus.NOT_FOUND, request);
        }


        Optional<Objective> optionalObjective = objectiveRepository.findById(Long.valueOf(mission.getObjective().getId()));

        objectiveRepository.save(mission.getObjective());

        Optional<GameType> optionalGameType = gameTypeRepository.findById(mission.getGameType().getId());


        Mission updatedMission = foundMission.get();
        updatedMission.setGameType(optionalGameType.get());
        updatedMission.setObjective(optionalObjective.get());
        updatedMission.setIsActive(mission.getIsActive());
        updatedMission.setIsDaily(mission.getIsDaily());
        updatedMission.setExperience(mission.getExperience());
        updatedMission.setEndDate(mission.getEndDate());
        updatedMission.setStartDate(mission.getStartDate());


        missionRepository.save(updatedMission);
        return new GlobalResponseHandler().handleResponse("Mision actualizada con éxito",
                updatedMission, HttpStatus.OK, request);
    }

    @DeleteMapping("/{missionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long missionId, HttpServletRequest request) {
        Optional<Mission> foundMission = missionRepository.findById(missionId);
        if(foundMission.isPresent()) {
            missionRepository.delete(foundMission.get());

            return new GlobalResponseHandler().handleResponse("Mision borrada con  éxito",
                    foundMission.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Mision id " + missionId + " no encontrado"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }



}
