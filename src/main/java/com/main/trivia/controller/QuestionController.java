package com.main.trivia.controller;

import com.main.trivia.model.Question;
import com.main.trivia.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/v1/questions")
    public @ResponseBody List<Question> getAllQuestions(){
        return questionService.getAllQuestions();
    }

    @GetMapping("/v1/questions/{id}")
    public @ResponseBody Question getQuestionById(@PathVariable long id){
        return questionService.getQuestionById(id);
    }

}
