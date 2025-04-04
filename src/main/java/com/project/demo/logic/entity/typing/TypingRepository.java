package com.project.demo.logic.entity.typing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypingRepository extends JpaRepository<Typing, Long> {
}
