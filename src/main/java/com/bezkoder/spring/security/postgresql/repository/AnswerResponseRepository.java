package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.AnswerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AnswerResponseRepository extends JpaRepository<AnswerResponse,Long> {

    @Query("SELECT ar FROM AnswerResponse ar WHERE ar.user.id = :userId AND ar.createdAt BETWEEN :startDate AND :endDate")
    List<AnswerResponse> findByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
