package com.project.demo.logic.entity.config;

import com.project.demo.logic.entity.config.Config;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

    Optional<Config> findByUser(User user);
}
