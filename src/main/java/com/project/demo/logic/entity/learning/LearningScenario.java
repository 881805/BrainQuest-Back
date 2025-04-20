package com.project.demo.logic.entity.learning;

import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.trivia.Option;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class LearningScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String story; // Historia generada por la IA

    private String question; // Pregunta reflexiva basada en la historia

    @ElementCollection
    private List<Option> options; // Opciones generadas por la IA

    private String selectedOption; // Respuesta elegida por el usuario

    @Column(length = 1000)
    private String feedback; // Feedback de la IA basado en la opción elegida

    private String topic; // Tema de aprendizaje (Ej: ética médica, reciclaje, etc.)

    private int step; // Paso del escenario progresivo

    private boolean completed;

    @ManyToOne
    private Game game;

    public LearningScenario() {}

    public LearningScenario(Long id, String story, String question, List<Option> options, String selectedOption, String feedback, String topic, int step, boolean completed, Game game) {
        this.id = id;
        this.story = story;
        this.question = question;
        this.options = options;
        this.selectedOption = selectedOption;
        this.feedback = feedback;
        this.topic = topic;
        this.step = step;
        this.completed = completed;
        this.game = game;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}