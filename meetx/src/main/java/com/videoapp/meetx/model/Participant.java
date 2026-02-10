package com.videoapp.meetx.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    private String id;
    private String token;
    private String roomId;
    private WebSocketSession session;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActivityAt;
    private boolean connected;

    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }
    
    public boolean isExpired(int timeoutMinutes) {
        return lastActivityAt != null && 
               lastActivityAt.plusMinutes(timeoutMinutes).isBefore(LocalDateTime.now());
    }
}
