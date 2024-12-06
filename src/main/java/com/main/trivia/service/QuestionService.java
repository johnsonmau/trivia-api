package com.main.trivia.service;

import com.main.trivia.model.Guess;
import com.main.trivia.model.IncorrectAnswer;
import com.main.trivia.model.Question;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuestionService {

    List<Question> getAllQuestions();
    Question getQuestionById(long id);
    List<IncorrectAnswer> getIncorrectAnswersByQId(long questionId);
    Question getRandomQuestion(String difficulty, String category);
    String solveQuestion(Guess guess);
}
