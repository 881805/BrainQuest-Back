package com.project.demo.logic.entity.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningRepository extends JpaRepository<LearningScenario, Long> {
    // Buscar un escenario por pregunta y tema (útil para evitar duplicados generados)
    Optional<LearningScenario> findByQuestionAndTopic(String question, String topic);

    // Obtener todos los escenarios de un tema en orden de paso
    List<LearningScenario> findByTopicOrderByStepNumberAsc(String topic);

    // Buscar el siguiente paso dentro de un tema
    Optional<LearningScenario> findByTopicAndStepNumber(String topic, Integer stepNumber);
}