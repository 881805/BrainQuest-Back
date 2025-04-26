package com.project.demo.logic.entity.trivia;

import jakarta.persistence.*;

@Embeddable
public class Option {
    private String text;
    private boolean isCorrect;

    public Option() {
    }

    public Option(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public Option(String text) {
        this.text = text;
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
}