package com.project.demo.logic.entity.aim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AimRepository extends JpaRepository<Aim, Integer> {
}
