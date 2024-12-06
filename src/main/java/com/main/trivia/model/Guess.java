package com.main.trivia.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Guess {

    @JsonProperty("qid")
    private long questionId;
    private String guess;

    public Guess() {
    }

    public Guess(long questionId, String guess) {
        this.questionId = questionId;
        this.guess = guess;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }
}
