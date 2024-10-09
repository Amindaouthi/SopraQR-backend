// src/main/java/com/bezkoder/spring/security/postgresql/controllers/VoteController.java
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    // Endpoint pour Liker
    @PostMapping("/like/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> like(
            @PathVariable Long entityId,
            @PathVariable String entityType,
            @RequestParam Long userId) {
        return voteService.like(userId, entityId, entityType);
    }

    // Endpoint pour Disliker
    @PostMapping("/dislike/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> dislike(
            @PathVariable Long entityId,
            @PathVariable String entityType,
            @RequestParam Long userId) {
        return voteService.dislike(userId, entityId, entityType);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getVoteStatus(
            @RequestParam Long entityId,
            @RequestParam String entityType,
            @RequestParam Long userId) {

        int voteValue = voteService.getVoteValue(userId, entityId, entityType);
        Integer totalVotesFromRepo = voteService.calculateUpdatedVotes(entityId, entityType);

        Map<String, Integer> response = new HashMap<>();
        response.put("value", voteValue);
        response.put("totalVotes", totalVotesFromRepo);

        return ResponseEntity.ok(response);
    }
}
