package com.project.demo.dto;

public class FeedbackResponse {
    private Long questionId;
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private String feedback;

    public FeedbackResponse(Long questionId, String question, String userAnswer, String correctAnswer, String feedback) {
        this.questionId = questionId;
        this.question = question;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.feedback = feedback;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getFeedback() {
        return feedback;
    }
}