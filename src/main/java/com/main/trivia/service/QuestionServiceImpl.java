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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService{

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
    public List<IncorrectAnswer> getIncorrectAnswersByQId(long questionId){
        return incorrectAnswerRepository.findAllByQuestionId(questionId);
    }

    @Override
    public ResponseEntity<?> getRandomQuestion(String difficulty, String category, String token){

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new Error("cant parse auth token"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body(new Error("user doesn't exist"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("charset", "utf-8");
        Question question = questionRepository.findRandomQuestion(difficulty, category);

        List<String> allAnswers;

        if (question.getType().equals(Question.QuestionType.MULTIPLE)){
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

        return ResponseEntity.status(200).headers(headers).body(question);
    }

    @Override
    public ResponseEntity<?> solveQuestion(Guess guess, String token) {

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        long questionId = guess.getQuestionId();
        String guessStr = guess.getGuess();
        Question question = questionRepository.findById(questionId).get();
        String correctAnswer = question.getCorrectAnswer();

        boolean correct = guessStr.equals(correctAnswer);

        if (correct) return ResponseEntity.status(200).body(new SolvedQuestionRes(true));
        return ResponseEntity.status(200).body(new SolvedQuestionRes(false));
    }

}
