package com.project.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.learning.LearningOption;
import com.project.demo.logic.entity.learning.LearningScenario;
import com.project.demo.service.LearningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LearningServiceTest {


    @InjectMocks
    private LearningService learningService;



    @Test
    public void testParseScenarioResponse() {
        String json = """
            {
              "narrative": "You are a developer learning about Java Streams.",
              "question": "Which method is used to filter elements in a Stream?",
              "correctAnswer": "filter()",
              "options": ["map()", "collect()", "filter()", "forEach()"]
            }
        """;

        String topic = "Java Streams";
        int step = 1;

        LearningScenario scenario = learningService.parseScenarioResponse(json, topic, step);

        assertEquals("You are a developer learning about Java Streams.", scenario.getNarrative());
        assertEquals("Which method is used to filter elements in a Stream?", scenario.getQuestion());
        assertEquals("filter()", scenario.getCorrectAnswer());
        assertEquals(topic, scenario.getTopic());
        assertEquals(step, scenario.getStepNumber());

        List<LearningOption> options = scenario.getOptions();
        assertEquals(4, options.size());

        boolean correctFound = options.stream().anyMatch(LearningOption::isCorrect);
        assertTrue(correctFound, "At least one option should be marked as correct");
    }
}
