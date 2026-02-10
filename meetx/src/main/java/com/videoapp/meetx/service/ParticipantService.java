package com.videoapp.meetx.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.videoapp.meetx.model.Participant;
import com.videoapp.meetx.util.TokenGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService {

    @Value("${webrtc.room.inactive-timeout-minutes}")
    private int inactiveTimeoutMinutes;

    private final Map<String, Participant> participants = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToParticipantId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToParticipantId = new ConcurrentHashMap<>();

    public Participant createParticipant(String roomId, WebSocketSession session) {

        String participantId = UUID.randomUUID().toString();
        String token = TokenGenerator.generateParticipantToken();

        Participant participant = Participant.builder().id(participantId).token(token).roomId(roomId).session(session).joinedAt(LocalDateTime.now()).lastActivityAt(LocalDateTime.now()).connected(true).build();

        participants.put(participantId, participant);
        tokenToParticipantId.put(token, participantId);
        sessionToParticipantId.put(session.getId(), participantId);

        log.info("Created participant: {} in room: {}", participantId, roomId);
        return participant;
    }

    public Participant getParticipant(String participantId) {
        return participants.get(participantId);
    }

    public Participant getParticipantByToken(String token) {
        String participantId = tokenToParticipantId.get(token);
        return participantId != null ? participants.get(participantId) : null;
    }

    public Participant getParticipantBySession(String sessionId) {
        String participantId = sessionToParticipantId.get(sessionId);
        return participantId != null ? participants.get(participantId) : null;
    }

    public List<Participant> getRoomParticipants(String roomId) {
        return participants.values().stream().filter(p -> p.getRoomId().equals(roomId)).collect(Collectors.toList());
    }

    public void updateActivity(String participantId) {
        Participant participant = participants.get(participantId);
        if (participant != null) {
            participant.updateActivity();
        }
    }

    public void removeParticipant(String participantId) {
        Participant participant = participants.remove(participantId);
        if (participant != null) {
            tokenToParticipantId.remove(participant.getToken());
            sessionToParticipantId.remove(participant.getSession().getId());
            log.info("Removed participant: {} from room: {}", participantId, participant.getRoomId());
        }
    }

    public void disconnectParticipant(String participantId) {
        Participant participant = participants.get(participantId);
        if (participant != null) {
            participant.setConnected(false);
            participant.updateActivity();
            log.info("Disconnected participant: {}", participantId);
        }
    }

    public boolean isValidToken(String roomId, String token) {
        Participant participant = getParticipantByToken(token);
        return participant != null && participant.getRoomId().equals(roomId);
    }

    public List<Participant> getExpiredParticipants() {
        return participants.values().stream().filter(p -> p.isExpired(inactiveTimeoutMinutes)).collect(Collectors.toList());
    }

    public int getParticipantCount(String roomId) {
        return (int) participants.values().stream().filter(p -> p.getRoomId().equals(roomId) && p.isConnected()).count();
    }
}
