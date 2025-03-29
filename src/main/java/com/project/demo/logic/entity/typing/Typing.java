package com.project.demo.logic.entity.typing;

import com.project.demo.logic.entity.game.Game;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Typing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String text;
    private int timeLimit; // Tiempo en segundos
    private String difficulty;
    private String category;

    @ElementCollection
    private List<String> hints; // Posibles pistas para ayudar en la escritura

    @ManyToOne
    private Game game;

    public Typing() {
    }

    public Typing(String text, int timeLimit, String difficulty, String category, List<String> hints, Game game) {
        this.text = text;
        this.timeLimit = timeLimit;
        this.difficulty = difficulty;
        this.category = category;
        this.hints = hints;
        this.game = game;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
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

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}