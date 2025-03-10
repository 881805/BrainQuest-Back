package com.project.demo.logic.entity.feedback;

import com.project.demo.logic.entity.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
