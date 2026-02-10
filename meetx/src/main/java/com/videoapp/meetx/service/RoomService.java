package com.videoapp.meetx.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.videoapp.meetx.exception.RoomFullException;
import com.videoapp.meetx.exception.RoomNotFoundException;
import com.videoapp.meetx.model.Participant;
import com.videoapp.meetx.model.Room;
import com.videoapp.meetx.util.Constants;
import com.videoapp.meetx.util.TokenGenerator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RoomService {

    @Value("${webrtc.room.max-participants}")
    private int maxParticipants;

    @Value("${webrtc.room.token-expiry-minutes}")
    private int tokenExpiryMinutes;

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final ParticipantService participantService;
    private final TokenService tokenService;

    public RoomService(ParticipantService participantService, @Lazy TokenService tokenService) {
        this.participantService = participantService;
        this.tokenService = tokenService;
    }

    public Room createRoom(String roomName) {
        String roomId = TokenGenerator.generateRoomId();
        String creatorToken = TokenGenerator.generateParticipantToken();
        
        Room room = Room.builder()
                .id(roomId)
                .name(roomName)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                .maxParticipants(maxParticipants)
                .creatorToken(creatorToken)
                .participantIds(new HashSet<>())
                .build();
        
        rooms.put(roomId, room);
        
        // Register the creator token with TokenService
        tokenService.registerCreatorToken(roomId, creatorToken);
        
        log.info("Created room: {} with name: {}", roomId, roomName);
        return room;
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomNotFoundException(Constants.ERR_ROOM_NOT_FOUND);
        }
        if (room.isExpired()) {
            removeRoom(roomId);
            throw new RoomNotFoundException("Room has expired");
        }
        return room;
    }

    public void addParticipantToRoom(String roomId, String participantId) {
        Room room = getRoom(roomId);
        
        if (room.isFull()) {
            throw new RoomFullException(Constants.ERR_ROOM_FULL);
        }
        
        room.addParticipant(participantId);
        log.info("Added participant: {} to room: {}", participantId, roomId);
    }

    public void removeParticipantFromRoom(String roomId, String participantId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removeParticipant(participantId);
            log.info("Removed participant: {} from room: {}", participantId, roomId);
            
            // Clean up empty rooms
            if (room.isEmpty()) {
                removeRoom(roomId);
            }
        }
    }

    public void removeRoom(String roomId) {
        Room room = rooms.remove(roomId);
        if (room != null) {
            // Clean up tokens for this room
            tokenService.removeRoomTokens(roomId);
            log.info("Removed room: {}", roomId);
        }
    }

    public List<String> getRoomParticipantIds(String roomId) {
        Room room = getRoom(roomId);
        return new ArrayList<>(room.getParticipantIds());
    }

    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    @Scheduled(fixedDelayString = "${webrtc.room.cleanup-interval-minutes}000", 
               initialDelayString = "${webrtc.room.cleanup-interval-minutes}000")
    public void cleanupExpiredRooms() {
        log.debug("Starting room cleanup task");
        
        List<String> expiredRoomIds = rooms.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .toList();
        
        expiredRoomIds.forEach(roomId -> {
            Room room = rooms.get(roomId);
            if (room != null) {
                // Remove all participants in the room
                room.getParticipantIds().forEach(participantService::removeParticipant);
                removeRoom(roomId);
                log.info("Cleaned up expired room: {}", roomId);
            }
        });
        
        // Also cleanup expired participants
        List<Participant> expiredParticipants = participantService.getExpiredParticipants();
        expiredParticipants.forEach(participant -> {
            removeParticipantFromRoom(participant.getRoomId(), participant.getId());
            participantService.removeParticipant(participant.getId());
            log.info("Cleaned up expired participant: {}", participant.getId());
        });
        
        log.debug("Room cleanup completed. Removed {} rooms and {} participants", 
                 expiredRoomIds.size(), expiredParticipants.size());
    }

    public Map<String, Object> getServerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", rooms.size());
        
        int totalParticipants = rooms.values().stream()
                .mapToInt(room -> room.getParticipantIds().size())
                .sum();
        stats.put("totalParticipants", totalParticipants);
        
        return stats;
    }
}