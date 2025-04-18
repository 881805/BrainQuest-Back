package com.project.demo.rest.aiConfiguration;

import com.project.demo.auth.UserService;
import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.user.User;
import com.project.demo.service.AiConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.demo.logic.entity.user.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/ai-configurations")
public class AiConfigurationController {

    @Autowired
    private AiConfigurationService aiConfigurationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<AiConfiguration> getAll() {
        return aiConfigurationService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<AiConfiguration> getByUser(@PathVariable Long userId) {
        return aiConfigurationService.findByUserId(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<AiConfiguration> create(@RequestBody AiConfiguration config) {
        if (config.getUser() == null || config.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findById(config.getUser().getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        config.setUser(user);
        return ResponseEntity.ok(aiConfigurationService.save(config));
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (aiConfigurationService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        aiConfigurationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}