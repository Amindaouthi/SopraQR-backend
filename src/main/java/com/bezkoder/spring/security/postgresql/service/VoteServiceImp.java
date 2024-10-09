// src/main/java/com/bezkoder/spring/security/postgresql/service/VoteServiceImp.java
package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VoteServiceImp implements VoteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerResponseRepository answerResponseRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public ResponseEntity<Map<String, Object>> vote(Long userId, Long entityId, String entityType, int value) {
        return null;
    }

    // Méthode pour gérer le Like
    @Override
    public ResponseEntity<Map<String, Object>> like(Long userId, Long entityId, String entityType) {
        return handleVote(userId, entityId, entityType, 1); // 1 pour le Like
    }

    // Méthode pour gérer le Dislike
    @Override
    public ResponseEntity<Map<String, Object>> dislike(Long userId, Long entityId, String entityType) {
        return handleVote(userId, entityId, entityType, 0); // 0 pour le Dislike
    }

    // Méthode commune pour gérer le Like ou Dislike
    // Méthode commune pour gérer le Like ou Dislike
    private ResponseEntity<Map<String, Object>> handleVote(Long userId, Long entityId, String entityType, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);

        if (vote != null) {
            if (vote.getValue() == value) {
                voteRepository.delete(vote);
                vote = null;
            } else {
                vote.setValue(value);
                voteRepository.save(vote);
            }
        } else {
            vote = new Vote();
            vote.setUser(user);
            vote.setEntityId(entityId);
            vote.setEntityType(entityType);
            vote.setValue(value);

            switch (entityType) {
                case "Question":
                    Question question = questionRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("Error: Question is not found."));
                    vote.setQuestion(question);
                    break;
                case "Answer":
                    Answer answer = answerRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("Error: Answer is not found."));
                    vote.setAnswer(answer);
                    break;
                case "AnswerResponse":
                    AnswerResponse answerResponse = answerResponseRepository.findById(entityId)
                            .orElseThrow(() -> new RuntimeException("Error: AnswerResponse is not found."));
                    vote.setAnswerResponse(answerResponse);
                    break;
                default:
                    throw new RuntimeException("Error: Invalid entityType.");
            }

            voteRepository.save(vote);
        }

        // Calculer les votes mis à jour
        int updatedVotes = calculateUpdatedVotes(entityId, entityType);

        // Préparer la réponse sous forme de Map pour la sérialisation JSON
        Map<String, Object> response = new HashMap<>();
        response.put("updatedVotes", updatedVotes);
        response.put("userVote", vote != null ? vote.getValue() : null);

        return ResponseEntity.ok(response);
    }


    @Override
    public int calculateUpdatedVotes(Long entityId, String entityType) {
        Integer sumVotes = null;

        switch (entityType) {
            case "Question":
                sumVotes = voteRepository.sumVotesByQuestionId(entityId);
                break;
            case "Answer":
                sumVotes = voteRepository.sumVotesByAnswerId(entityId);
                break;
            case "AnswerResponse":
                sumVotes = voteRepository.sumVotesByAnswerResponseId(entityId);
                break;
            default:
                throw new RuntimeException("Error: Invalid entityType.");
        }

        return (sumVotes != null) ? sumVotes : 0;
    }

    @Override
    public int getVoteValue(Long userId, Long entityId, String entityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);
        return vote != null ? vote.getValue() : -1; // Assuming -1 means no vote
    }
}
