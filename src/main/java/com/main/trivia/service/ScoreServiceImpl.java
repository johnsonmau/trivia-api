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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ScoreServiceImpl  implements ScoreService{

    private static final Logger LOGGER = Logger.getLogger(ScoreServiceImpl.class.getName());

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
            LOGGER.log(Level.INFO, "Extracted username from token: {0}", username);

            User user = userRepository.findByUsername(username);
            LOGGER.log(Level.INFO, "User found: {0}", user.getUsername());

            Score scoreToSave = score;
            scoreToSave.setUser(user);
            scoreToSave = scoreRepository.save(scoreToSave);
            LOGGER.log(Level.INFO, "Score saved: {0}", scoreToSave.getScore());

            List<Score> existingScores = user.getScores();
            existingScores.add(scoreToSave);

            user.setScores(existingScores);
            user.updateLastActive();
            user.setGamesPlayed(user.getGamesPlayed() + 1);
            LOGGER.log(Level.INFO, "Updated user stats: gamesPlayed={0}", user.getGamesPlayed());

            userRepository.save(user);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error saving score", ex);
            return ResponseEntity.status(500).body(new Error(ex.getMessage()));
        }

        return ResponseEntity.status(200).body("success");
    }

    @Override
    public ResponseEntity<?> getTop25Scores() {
        LOGGER.log(Level.INFO, "Fetching top 25 scores");
        List<Score> top25Scores = scoreRepository.findTop25();
        List<Leader> top25Leaders = new ArrayList<>();

        for (Score score : top25Scores) {
            Leader leader = new Leader();
            leader.setScore(score.getScore());
            leader.setDate(score.getDate());
            User user = userRepository.findById(score.getUserId()).orElse(null);

            if (user != null) {
                leader.setCountryCd(user.getCountryCd());
                leader.setUsername(user.getUsername());
            } else {
                LOGGER.log(Level.WARNING, "User not found for score ID: {0}", score.getUserId());
            }

            top25Leaders.add(leader);
        }

        return ResponseEntity.status(200).body(top25Leaders);
    }
}
