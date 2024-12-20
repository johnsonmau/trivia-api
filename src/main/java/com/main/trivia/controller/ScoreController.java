package com.main.trivia.controller;

import com.main.trivia.model.Score;
import com.main.trivia.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/scores")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Allow requests from any origin
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping
    public ResponseEntity<?> getUsersScores(@RequestHeader("Authorization") String token) {
        return scoreService.getUsersScores(token);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveScore(@RequestHeader("Authorization") String token, @RequestBody Score score) {
        return scoreService.saveScore(token, score);
    }
}
