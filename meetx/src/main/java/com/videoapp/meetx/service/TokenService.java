package com.videoapp.meetx.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.videoapp.meetx.exception.InvalidTokenException;
import com.videoapp.meetx.model.Participant;
import com.videoapp.meetx.model.Room;
import com.videoapp.meetx.util.Constants;
import com.videoapp.meetx.util.TokenGenerator;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RoomService roomService;
    private final ParticipantService participantService;
    
    // Store valid tokens per room (creator token + join tokens)
    private final ConcurrentHashMap<String, Set<String>> roomTokens = new ConcurrentHashMap<>();

    public String generateRoomToken(String roomId) {
        String token = TokenGenerator.generateParticipantToken();
        
        // Add token to valid tokens for this room
        roomTokens.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(token);
        
        log.debug("Generated token for room: {}", roomId);
        return token;
    }
    
    public void registerCreatorToken(String roomId, String token) {
        roomTokens.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(token);
        log.debug("Registered creator token for room: {}", roomId);
    }

    public void validateToken(String roomId, String token) {
        Room room = roomService.getRoom(roomId);
        
        if (room == null) {
            throw new InvalidTokenException(Constants.ERR_ROOM_NOT_FOUND);
        }
        
        if (room.isExpired()) {
            throw new InvalidTokenException("Room has expired");
        }
        
        // Check if token is valid for this room
        Set<String> validTokens = roomTokens.get(roomId);
        if (validTokens == null || !validTokens.contains(token)) {
            throw new InvalidTokenException(Constants.ERR_INVALID_TOKEN);
        }
        
        log.debug("Token validated for room: {}", roomId);
    }

    public Participant getParticipantByToken(String token) {
        return participantService.getParticipantByToken(token);
    }
    
    public void removeRoomTokens(String roomId) {
        roomTokens.remove(roomId);
        log.debug("Removed all tokens for room: {}", roomId);
    }
}