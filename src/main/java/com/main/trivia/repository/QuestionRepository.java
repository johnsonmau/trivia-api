package com.main.trivia.repository;

import com.main.trivia.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Custom query methods can be added here if needed
}
