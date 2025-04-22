package com.project.demo.logic.entity.learning;


import com.project.demo.logic.entity.game.Game;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class LearningScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String narrative;

    private String question;

    private String correctAnswer;

    @ElementCollection
    private List<LearningOption> options;

    @Column(length = 1000)
    private String feedback;

    private String userAnswer;

    @ElementCollection
    private List<String> blockedOptions;

    @ManyToOne
    private Game game;

    private Integer stepNumber;

    private String topic;

    @Column(nullable = false)
    private boolean completed = false;

    public boolean isCompleted() {
        return completed;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        this.completed = userAnswer != null && userAnswer.equalsIgnoreCase(this.correctAnswer);
    }

    public LearningScenario() {
    }

    public LearningScenario(Long id, String narrative, String question, String correctAnswer, List<LearningOption> options, String feedback, String userAnswer, List<String> blockedOptions, Game game, Integer stepNumber, String topic, boolean completed) {
        this.id = id;
        this.narrative = narrative;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.feedback = feedback;
        this.userAnswer = userAnswer;
        this.blockedOptions = blockedOptions;
        this.game = game;
        this.stepNumber = stepNumber;
        this.topic = topic;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<LearningOption> getOptions() {
        return options;
    }

    public void setOptions(List<LearningOption> options) {
        this.options = options;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public List<String> getBlockedOptions() {
        return blockedOptions;
    }

    public void setBlockedOptions(List<String> blockedOptions) {
        this.blockedOptions = blockedOptions;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}