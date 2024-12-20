package com.main.trivia.model;

public class SolvedQuestionRes {
    private boolean correctAnswer;

    public SolvedQuestionRes(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
