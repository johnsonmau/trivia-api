package com.main.trivia.service;

import com.main.trivia.model.Question;
import com.main.trivia.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionRepository repo;

    @Override
    public List<Question> getAllQuestions() {
        return repo.findAll();
    }

    public Question getQuestionById(long id) {
        return repo.findById(id).get();
    }
}
