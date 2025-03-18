package com.main.trivia.controller;

import com.main.trivia.model.Score;
import com.main.trivia.service.ScoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/scores")
@CrossOrigin(origins = "${cors.allowed-origins}", allowedHeaders = "*")  // Read from application.properties
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @PostMapping("/save")
    public @ResponseBody ResponseEntity<?> saveScore(@RequestHeader("Authorization") String token,
                                                     @RequestBody Score score, HttpServletRequest request) {

//        String origin = request.getHeader("Origin");
//        if (origin == null || !origin.equals("http://localhost:56088")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        return scoreService.saveScore(token, score);
    }

    @GetMapping("/leaders/25")
    public @ResponseBody ResponseEntity<?> getTop25() {
        return scoreService.getTop25Scores();
    }
}
