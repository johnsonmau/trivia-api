package com.main.trivia.service;

import com.main.trivia.model.Score;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ScoreService {
    ResponseEntity<List<Score>> getUsersScores(String token);
    ResponseEntity<?> saveScore(String token, Score score);
}
