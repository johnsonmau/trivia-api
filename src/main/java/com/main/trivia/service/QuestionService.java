package com.main.trivia.service;

import com.main.trivia.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuestionService {

    List<Question> getAllQuestions();
    Question getQuestionById(long id);
    List<IncorrectAnswer> getIncorrectAnswersByQId(long questionId);
    ResponseEntity<?> getRandomQuestion(String difficulty, String category, String token);
    ResponseEntity<?>  solveQuestion(Guess guess, String token);

}
