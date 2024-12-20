package com.main.trivia.service;

import com.main.trivia.model.Score;
import com.main.trivia.model.User;
import com.main.trivia.repository.ScoreRepository;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl  implements ScoreService{

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public ResponseEntity<List<Score>> getUsersScores(String token) {

        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        return ResponseEntity.status(200).body(scoreRepository.findAllByUserIdOrderByDateDesc(user.getId()));

    }

    @Override
    public ResponseEntity<?> saveScore(String token, Score score) {

        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username);

        Score scoreToSave = score;
        scoreToSave.setUser(user);

        scoreToSave = scoreRepository.save(scoreToSave);

        List<Score> existingScores = user.getScores();
        existingScores.add(scoreToSave);

        user.setScores(existingScores);

        return ResponseEntity.status(200).body("worked");
    }
}
