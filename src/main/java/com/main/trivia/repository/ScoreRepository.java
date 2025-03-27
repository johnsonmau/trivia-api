package com.main.trivia.repository;

import com.main.trivia.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
   // List<Score> findAllByUserIdOrderByDateDesc(Long userId);

    @Query(value = """
        SELECT *
        FROM scores
        WHERE (:difficulty IS NULL OR difficulty = :difficulty)
          AND (:category IS NULL OR category = :category)
        ORDER BY score DESC
        LIMIT 25
        """, nativeQuery = true)
    List<Score> findTop25(@Param("difficulty") String difficulty, @Param("category") String category);
}

