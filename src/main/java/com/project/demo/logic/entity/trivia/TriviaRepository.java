package com.project.demo.logic.entity.trivia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaRepository extends JpaRepository<TriviaQuestion, Long> {
}
