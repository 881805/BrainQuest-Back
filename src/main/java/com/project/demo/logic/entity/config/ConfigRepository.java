package com.project.demo.logic.entity.config;

import com.project.demo.logic.entity.config.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

}
