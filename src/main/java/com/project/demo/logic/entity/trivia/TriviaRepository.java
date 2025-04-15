package com.project.demo.logic.entity.trivia;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaRepository extends JpaRepository<TriviaQuestion, Long> {
    Optional<TriviaQuestion> findByQuestionAndCategoryAndDifficulty(String question, String category, String difficulty);
    List<TriviaQuestion> findByCategoryAndDifficulty(String category, String difficulty);
}
