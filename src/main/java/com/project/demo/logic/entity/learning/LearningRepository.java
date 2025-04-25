package com.project.demo.logic.entity.learning;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningRepository extends JpaRepository<LearningScenario, Long> {

    Optional<LearningScenario> findByQuestionAndTopic(String question, String topic);

    List<LearningScenario> findByTopicOrderByStepNumberAsc(String topic);

    Optional<LearningScenario> findByTopicAndStepNumber(String topic, Integer stepNumber);

    Page<LearningScenario> findByTopic(String topic, Pageable pageable);

}