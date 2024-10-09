// src/main/java/com/bezkoder/spring/security/postgresql/service/VoteService.java
package com.bezkoder.spring.security.postgresql.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface VoteService {
    ResponseEntity<Map<String, Object>> vote(Long userId, Long entityId, String entityType, int value);
    ResponseEntity<Map<String, Object>> like(Long userId, Long entityId, String entityType);
    ResponseEntity<Map<String, Object>> dislike(Long userId, Long entityId, String entityType);
    int getVoteValue(Long userId, Long entityId, String entityType);
    int calculateUpdatedVotes(Long entityId, String entityType);
}
