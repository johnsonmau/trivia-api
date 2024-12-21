package com.main.trivia.service;

import com.main.trivia.model.Error;
import com.main.trivia.model.Leader;
import com.main.trivia.model.Score;
import com.main.trivia.model.User;
import com.main.trivia.repository.ScoreRepository;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<?> saveScore(String token, Score score) {

        try {

            String username = jwtUtil.extractUsername(token.substring(7));
            User user = userRepository.findByUsername(username);

            Score scoreToSave = score;
            scoreToSave.setUser(user);

            scoreToSave = scoreRepository.save(scoreToSave);

            List<Score> existingScores = user.getScores();
            existingScores.add(scoreToSave);

            user.setScores(existingScores);
            user.updateLastActive();
            user.setGamesPlayed(user.getGamesPlayed() + 1);

            userRepository.save(user);

        } catch (Exception ex){
            return ResponseEntity.status(500).body(new Error(ex.getMessage()));
        }

        return ResponseEntity.status(200).body("success");
    }

    @Override
    public ResponseEntity<?> getTop25Scores() {
        List<Score> top25Scores = scoreRepository.findTop25();
        List<Leader> top25Leaders = new ArrayList<>();

        for (Score score : top25Scores) {
            Leader leader = new Leader();
            leader.setScore(score.getScore());
            leader.setDate(score.getDate());
            User user = userRepository.findById(score.getUserId()).get();
            leader.setCountryCd(user.getCountryCd());
            leader.setUsername(user.getUsername());
            top25Leaders.add(leader);
        }

        return ResponseEntity.status(200).body(top25Leaders);
    }
}
