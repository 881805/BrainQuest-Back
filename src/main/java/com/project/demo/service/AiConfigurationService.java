package com.project.demo.service;

import com.project.demo.logic.entity.aiConfiguration.AiConfiguration;
import com.project.demo.logic.entity.aiConfiguration.AiConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiConfigurationService {

    @Autowired
    private AiConfigurationRepository aiConfigurationRepository;

    public List<AiConfiguration> findAll() {
        return aiConfigurationRepository.findAll();
    }

    public List<AiConfiguration> findByUserId(Long userId) {
        return aiConfigurationRepository.findByUserId(userId);
    }

    public AiConfiguration findById(Long id) {
        return aiConfigurationRepository.findById(id).orElse(null);
    }

    public AiConfiguration save(AiConfiguration config) {
        return aiConfigurationRepository.save(config);
    }

    public void deleteById(Long id) {
        aiConfigurationRepository.deleteById(id);
    }
}