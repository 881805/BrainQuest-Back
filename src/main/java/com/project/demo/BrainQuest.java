package com.project.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrainQuest {

	public static void main(String[] args) {
		SpringApplication.run(BrainQuest.class, args);
	}

}
