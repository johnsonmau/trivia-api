package com.main.trivia.service;

import com.main.trivia.model.Question;

import java.util.List;

public interface QuestionService {

    List<Question> getAllQuestions();
    Question getQuestionById(long id);
}
