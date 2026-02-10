package com.videoapp.meetx.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Builder.Default
    private Set<String> participantIds = new HashSet<>();
    
    @Builder.Default
    private int maxParticipants = 2;
    
    private String creatorToken;

    public boolean isFull() {
        return participantIds.size() >= maxParticipants;
    }

    public boolean isExpired(){
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public void addParticipant(String participantId) {
        participantIds.add(participantId);
    }

    public void removeParticipant(String prticipantId) {
        participantIds.remove(prticipantId);
    }

    public boolean isEmpty() {
        return participantIds.isEmpty();
    }
}
