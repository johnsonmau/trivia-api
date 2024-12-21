package com.main.trivia.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.main.trivia.util.CustomDateSerializer;

import java.time.ZonedDateTime;

public class Leader {

    private String username;
    private int score;
    private String countryCd;

    @JsonSerialize(using = CustomDateSerializer.class)
    private ZonedDateTime date;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }
}
