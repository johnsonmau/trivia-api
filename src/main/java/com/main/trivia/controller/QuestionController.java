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
@CrossOrigin(origins = "*", allowedHeaders = "*") // Allow requests from any origin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public @ResponseBody List<Question> getAllQuestions(){
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public @ResponseBody Question getQuestionById(@PathVariable long id){
        return questionService.getQuestionById(id);
    }

    @GetMapping("/incorrect/{questionId}")
    public @ResponseBody List<IncorrectAnswer> getIncorrectAnswersByQId(@PathVariable long questionId){
        return questionService.getIncorrectAnswersByQId(questionId);
    }

    @GetMapping("/random")
    public @ResponseBody ResponseEntity<Question> getRandomQuestion(@RequestParam(required = false) String difficulty,
                                                                    @RequestParam(required = false) String category){
        return questionService.getRandomQuestion(difficulty, category);
    }

    @PostMapping("/solve")
    public ResponseEntity<?> solveQuestion(@RequestBody Guess guess,
                                                           @RequestHeader("Authorization") String token){
        return questionService.solveQuestion(guess, token);
    }
}
