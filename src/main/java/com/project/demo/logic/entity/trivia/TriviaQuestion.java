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
    private List<String> options;

    private String difficulty;
    private String category;
    @ManyToOne
    private Game game;
    public TriviaQuestion() {

    }

    public TriviaQuestion(Long id, String question, String correctAnswer, List<String> options, String difficulty, String category) {
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.difficulty = difficulty;
        this.category = category;
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
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
}