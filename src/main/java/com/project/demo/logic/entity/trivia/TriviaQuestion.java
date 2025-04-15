package com.project.demo.logic.entity.trivia;

import com.project.demo.logic.entity.game.Game;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class TriviaQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String correctAnswer;

    @ElementCollection
    private List<Option> options;

    private String difficulty;

    private String category;

    @ManyToOne
    private Game game;

    private String userAnswer;

    @Column(length = 1000)
    private String feedback;

    public TriviaQuestion() {}

    public TriviaQuestion(Long id, String question, String correctAnswer, List<Option> options, String difficulty, String category, Game game, String userAnswer, String feedback) {
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.difficulty = difficulty;
        this.category = category;
        this.game = game;
        this.userAnswer = userAnswer;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}