package com.main.trivia.controller;

import com.main.trivia.model.*;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/questions")
@CrossOrigin(origins = "${cors.allowed-origins}", allowedHeaders = "*")  // Read from application.properties
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //@GetMapping
    public @ResponseBody List<Question> getAllQuestions(){
        return questionService.getAllQuestions();
    }

    //@GetMapping("/{id}")
    public @ResponseBody Question getQuestionById(@PathVariable long id){
        return questionService.getQuestionById(id);
    }

    //@GetMapping("/incorrect/{questionId}")
    public @ResponseBody List<IncorrectAnswer> getIncorrectAnswersByQId(@PathVariable long questionId){
        return questionService.getIncorrectAnswersByQId(questionId);
    }

    @GetMapping("/random")
    public @ResponseBody ResponseEntity<?> getRandomQuestion(@RequestParam(required = false) String difficulty,
                                                                    @RequestParam(required = false) String category,
                                                                    @RequestHeader("Authorization") String token){
        return questionService.getRandomQuestion(difficulty, category, token);
    }

    @PostMapping("/solve")
    public ResponseEntity<?> solveQuestion(@RequestBody Guess guess,
                                                           @RequestHeader("Authorization") String token){
        return questionService.solveQuestion(guess, token);
    }
}
