package com.main.trivia.service;

import com.main.trivia.model.*;
import com.main.trivia.model.Error;
import com.main.trivia.repository.IncorrectAnswerRepository;
import com.main.trivia.repository.QuestionRepository;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final String[] categories = {
            "Mixed",
            "General Knowledge",
            "Entertainment: Books",
            "Entertainment: Board Games",
            "Entertainment: Cartoons",
            "Entertainment: Comics",
            "Entertainment: Film",
            "Entertainment: Japanese Anime",
            "Entertainment: Music",
            "Entertainment: Musicals & Theatres",
            "Entertainment: Television",
            "Entertainment: Video Games",
            "Geography",
            "History",
            "Mythology",
            "Politics",
            "Science & Nature",
            "Science: Computers",
            "Science: Gadgets",
            "Science: Mathematics",
            "Sports",
            "Vehicles",
            "Art",
            "Animals",
            "Celebrities"
    };

    private final String[] difficulties = {"Mixed", "Easy", "Medium", "Hard"};

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private IncorrectAnswerRepository incorrectAnswerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(long id) {
        return questionRepository.findById(id).get();
    }

    @Override
    public List<IncorrectAnswer> getIncorrectAnswersByQId(long questionId) {
        return incorrectAnswerRepository.findAllByQuestionId(questionId);
    }

    @Override
    public ResponseEntity<?> getRandomQuestion(String difficulty, String category, String token) {

        logger.info("Received request for random question. Difficulty: {}, Category: {}", difficulty, category);

        if (token == null || token.trim().isEmpty()) {
            logger.error("Unauthorized access attempt: Token is null or empty");
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            logger.error("Failed to parse authorization token", ex);
            return ResponseEntity.badRequest().body(new Error("cant parse auth token"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            logger.error("User with username {} does not exist", username);
            return ResponseEntity.badRequest().body(new Error("user doesn't exist"));
        }

        if (difficulty != null) {
            if (Arrays.asList(difficulties).contains(difficulty) == false || difficulty.equalsIgnoreCase("mixed")){
                difficulty = null;
            }
        }

        if (category != null) {
            if (Arrays.asList(categories).contains(category) == false || category.equalsIgnoreCase("mixed")){
                category = null;
            } else {
                if (category.equalsIgnoreCase("Entertainment: Japanese Anime")){
                    category = "Entertainment: Japanese Anime &amp; Manga";
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("charset", "utf-8");
        Question question = questionRepository.findRandomQuestion(difficulty, category);

        logger.info("Random question fetched: {}", question);

        List<String> allAnswers;

        if (question.getType().equals(Question.QuestionType.MULTIPLE)) {
            String correctAnswer = question.getCorrectAnswer();
            List<IncorrectAnswer> incorrectAnswers = getIncorrectAnswersByQId(question.getId());
            allAnswers = new ArrayList<>();
            allAnswers.add(correctAnswer);

            for (IncorrectAnswer incorrectAnswer : incorrectAnswers) {
                allAnswers.add(incorrectAnswer.getAnswer());
            }

            Collections.shuffle(allAnswers);

        } else {
            allAnswers = new ArrayList<>();
            allAnswers.add("True");
            allAnswers.add("False");
        }

        question.setAllAnswers(allAnswers);
        logger.info("Returning question with shuffled answers: {}", allAnswers);

        return ResponseEntity.status(200).headers(headers).body(question);
    }

    @Override
    public ResponseEntity<?> solveQuestion(Guess guess, String token) {

        logger.info("Received guess: {}", guess);

        if (token == null || token.trim().isEmpty()) {
            logger.error("Unauthorized access attempt: Token is null or empty");
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            logger.error("Failed to parse authorization token", ex);
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            logger.error("User with username {} does not exist", username);
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        long questionId = guess.getQuestionId();
        String guessStr = guess.getGuess();
        Question question = questionRepository.findById(questionId).get();
        String correctAnswer = question.getCorrectAnswer();

        boolean correct = guessStr.equals(correctAnswer);
        logger.info("User guessed: {}, Correct answer: {}, Result: {}", guessStr, correctAnswer, correct);

        if (correct) {
            return ResponseEntity.status(200).body(new SolvedQuestionRes(true));
        }
        return ResponseEntity.status(200).body(new SolvedQuestionRes(false));
    }

}
