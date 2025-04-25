package com.project.demo.rest.aiConfiguration;

import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.user.User;
import com.project.demo.service.AiConfigurationService;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AiConfiguration>> getMyConfigurations(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AiConfiguration> configs = aiConfigurationService.findByUserId(user.getId());
        return ResponseEntity.ok(configs);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiConfiguration> create(@RequestBody AiConfiguration config, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || config.getConfiguracion() == null) {
            return ResponseEntity.badRequest().build();
        }

        config.setUser(user);
        return ResponseEntity.ok(aiConfigurationService.save(config));
    }


    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<AiConfiguration> update(@PathVariable Long id, @RequestBody AiConfiguration updatedConfig) {
        AiConfiguration existing = aiConfigurationService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Solo actualizamos el campo permitido
        existing.setConfiguracion(updatedConfig.getConfiguracion());

        return ResponseEntity.ok(aiConfigurationService.save(existing));
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
