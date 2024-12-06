package com.main.trivia.repository;

import com.main.trivia.model.IncorrectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncorrectAnswerRepository extends JpaRepository<IncorrectAnswer, Long> {
    // Custom query methods for incorrect answers can be added here
}
