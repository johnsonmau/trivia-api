package com.main.trivia.service;

import com.main.trivia.model.Guess;
import com.main.trivia.model.IncorrectAnswer;
import com.main.trivia.model.Question;
import com.main.trivia.repository.IncorrectAnswerRepository;
import com.main.trivia.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private IncorrectAnswerRepository incorrectAnswerRepository;

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
    public Question getRandomQuestion(String difficulty, String category){
        return questionRepository.findRandomQuestion(difficulty, category);
    }

    @Override
    public String solveQuestion(Guess guess) {
        long questionId = guess.getQuestionId();
        String guessStr = guess.getGuess();
        Question question = questionRepository.findById(questionId).get();
        String correctAnswer = question.getCorrectAnswer();

        boolean correct = guessStr.equals(correctAnswer);

        if (correct) return "Correct answer";
        return "Incorrect answer";
    }

}
