package com.project.demo.rest.userachievement;

import com.project.demo.logic.entity.achievement.Achievement;
import com.project.demo.logic.entity.achievement.AchievementRepository;
import com.project.demo.logic.entity.aim.AimRepository;
import com.project.demo.logic.entity.game.GameRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.userachievement.UserAchievement;
import com.project.demo.logic.entity.userachievement.UserAchievementRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/userachievement")
@RestController
public class UserAchievementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private AimRepository aimRepository;

    @Autowired
    private GameRepository gameRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createAchievement(@RequestBody UserAchievement userAchievement, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findById(userAchievement.getUser().getId());
        Optional<Achievement> optionalAchievement = achievementRepository.findById(Long.valueOf(userAchievement.getAchievement().getId()));

        if (optionalUser.isEmpty() || optionalAchievement.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "User or Achievement not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        userAchievement.setUser(optionalUser.get());
        userAchievement.setAchievement(optionalAchievement.get());

        UserAchievement saved = userAchievementRepository.save(userAchievement);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{achievementId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> updateAchievement(@PathVariable Long achievementId, @RequestBody UserAchievement achievement, HttpServletRequest request) {
        Optional<UserAchievement> foundAchievement = userAchievementRepository.findById(achievementId);

        if(foundAchievement.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Achievement ID " + achievementId + " not found.",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        UserAchievement updatedAchievement = foundAchievement.get();
        updatedAchievement.setCompletedAt(achievement.getCompletedAt());
        updatedAchievement.setCompleted(achievement.isCompleted());
        updatedAchievement.setProgress(achievement.getProgress());

        // Validar null pointer y lógica de completado
        if(updatedAchievement.getAchievement() != null &&
                updatedAchievement.getAchievement().getAim() != null &&
                achievement.getProgress() >= updatedAchievement.getAchievement().getAim().getValue() &&
                Boolean.TRUE.equals(updatedAchievement.getAchievement())) {

            User foundUser = updatedAchievement.getUser();
            if(foundUser != null) {
                foundUser.setExperience(foundUser.getExperience() + updatedAchievement.getAchievement().getExperience());
                userRepository.save(foundUser);
                updatedAchievement.setCompleted(true);
            }
        }

        userAchievementRepository.save(updatedAchievement);
        return new GlobalResponseHandler().handleResponse(
                updatedAchievement.isCompleted() ?
                        "Achievement successfully completed" :
                        "Achievement progress updated",
                updatedAchievement,
                HttpStatus.OK,
                request
        );
    }

    @DeleteMapping("/{userachievementId}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long achievementId, HttpServletRequest request) {
        Optional<UserAchievement> foundAchievement = userAchievementRepository.findById(achievementId);
        if(foundAchievement.isPresent()) {
            userAchievementRepository.delete(foundAchievement.get());

            return new GlobalResponseHandler().handleResponse("Achievement deleted successfully",
                    foundAchievement.get(), HttpStatus.OK, request);
        } else{
            return new GlobalResponseHandler().handleResponse("Achievement id " + achievementId + " not found.",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
