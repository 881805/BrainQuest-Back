package com.project.demo.logic.entity.aiConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiConfigurationRepository extends JpaRepository<AiConfiguration, Long> {
    List<AiConfiguration> findByUserId(Long userId);
}