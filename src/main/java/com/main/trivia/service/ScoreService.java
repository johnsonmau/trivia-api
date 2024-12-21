package com.main.trivia.service;

import com.main.trivia.model.Score;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ScoreService {
    ResponseEntity<?> saveScore(String token, Score score);
    ResponseEntity<?> getTop25Scores();
}
