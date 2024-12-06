package com.main.trivia.repository;

import com.main.trivia.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = """
            SELECT * 
            FROM questions 
            WHERE (:difficulty IS NULL OR difficulty = :difficulty) 
              AND (:category IS NULL OR category = :category) 
            ORDER BY RAND() 
            LIMIT 1
            """, nativeQuery = true)
    Question findRandomQuestion(
            @Param("difficulty") String difficulty,
            @Param("category") String category
    );
}
