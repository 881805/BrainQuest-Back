package com.project.demo.rest.achievement;

import com.project.demo.dto.AchievementDTO;
import com.project.demo.logic.entity.Objective.Objective;
import com.project.demo.logic.entity.Objective.ObjectiveRepository;
import com.project.demo.logic.entity.achievement.Achievement;
import com.project.demo.logic.entity.achievement.AchievementRepository;
import com.project.demo.logic.entity.aim.Aim;
import com.project.demo.logic.entity.aim.AimRepository;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.gameType.GameType;
import com.project.demo.logic.entity.gameType.GameTypeRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.project.demo.logic.entity.http.Meta;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/achievements")
@RestController
public class AchievementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AimRepository aimRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createAchievement(@RequestBody Achievement achievement, HttpServletRequest request) {
        // Verificar si el usuario existe
        Optional<User> optionalUser = userRepository.findById(achievement.getCreatedBy().getId());
        if (optionalUser.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "User with ID " + achievement.getCreatedBy().getId() + " not found",
                    HttpStatus.NOT_FOUND, request
            );
        }

        // Verificar si el tipo de juego existe
        Optional<GameType> optionalGameType = gameTypeRepository.findById(achievement.getGameType().getId());
        if (optionalGameType.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "GameType with ID " + achievement.getGameType().getId() + " not found",
                    HttpStatus.NOT_FOUND, request
            );
        }


        // Manejar el Aim (objetivo)
        if(achievement.getAim() != null) {
            // Si es un nuevo Aim (sin ID), establecer valores por defecto
            if(achievement.getAim().getId() == null) {
                achievement.getAim().setActive(true);
            }
            aimRepository.save(achievement.getAim());
        }

        // Asignar los valores encontrados
        achievement.setCreatedBy(optionalUser.get());
        achievement.setGameType(optionalGameType.get());

        // Guardar el logro
        Achievement savedAchievement = achievementRepository.save(achievement);

        return new ResponseEntity<>(savedAchievement, HttpStatus.CREATED);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10")int size,
                                    HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Achievement> achievementPage = achievementRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(achievementPage.getTotalPages());
        meta.setTotalElements(achievementPage.getTotalElements());
        meta.setPageNumber(achievementPage.getNumber() + 1);
        meta.setPageSize(achievementPage.getSize());
        

        return new GlobalResponseHandler().handleResponse("Achievement retrieve succesfully",
                achievementPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{achievementId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAchievementById(@PathVariable Long achievementId, HttpServletRequest request) {
        Optional<Achievement> optionalAchievement = achievementRepository.findById(achievementId);

        if (optionalAchievement.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Achievement with ID " + achievementId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Achievement achievement = optionalAchievement.get();

        return new GlobalResponseHandler().handleResponse(
                "Achievement retrieved successfully",
                achievement,
                HttpStatus.OK,
                request
        );
    }

    @PutMapping("/{achievementId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> updateAchievement(@PathVariable Long achievementId,
                                               @RequestBody Achievement achievement,
                                               HttpServletRequest request) {
        Optional<Achievement> foundAchievement = achievementRepository.findById(achievementId);

        if (foundAchievement.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Achievement ID " + achievementId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Optional<Aim> optionalAim = aimRepository.findById(achievement.getAim().getId());
        if (optionalAim.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Aim with ID " + achievement.getAim().getId() + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Optional<GameType> optionalGameType = gameTypeRepository.findById(achievement.getGameType().getId());
        if (optionalGameType.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "GameType with ID " + achievement.getGameType().getId() + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        Achievement updatedAchievement = foundAchievement.get();
        updatedAchievement.setName(achievement.getName());
        updatedAchievement.setDescription(achievement.getDescription());
        updatedAchievement.setExperience(achievement.getExperience());
        updatedAchievement.setGameType(optionalGameType.get());
        updatedAchievement.setAim(optionalAim.get());
        updatedAchievement.setCreatedBy(achievement.getCreatedBy());

        achievementRepository.save(updatedAchievement);

        AchievementDTO dto = AchievementDTO.from(updatedAchievement);

        return new GlobalResponseHandler().handleResponse(
                "Achievement updated successfully",
                dto,
                HttpStatus.OK,
                request
        );
    }

    @DeleteMapping("/{achievementId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long achievementId, HttpServletRequest request) {
        Optional<Achievement> foundAchievement = achievementRepository.findById(achievementId);
        if(foundAchievement.isPresent()) {
            achievementRepository.delete(foundAchievement.get());

            return new GlobalResponseHandler().handleResponse("Achievement deleted",
                    foundAchievement.get(), HttpStatus.OK, request);
        } else{
            return new GlobalResponseHandler().handleResponse("Achievement id " + achievementId + " not found." ,
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
