package com.main.trivia.repository;

import com.main.trivia.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
   // List<Score> findAllByUserIdOrderByDateDesc(Long userId);

    @Query(value = """
            SELECT *
            FROM scores
            ORDER BY scores.score DESC
            LIMIT 25
            """, nativeQuery = true)
    List<Score> findTop25();
}

