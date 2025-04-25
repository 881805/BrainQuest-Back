package com.project.demo.logic.entity.learning;

import jakarta.persistence.Embeddable;

@Embeddable
public class LearningOption {
    private String text;
    private boolean isCorrect;
    private boolean isBlocked;

    public LearningOption() {
    }

    public LearningOption(String text, boolean isCorrect, boolean isBlocked) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.isBlocked = isBlocked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}