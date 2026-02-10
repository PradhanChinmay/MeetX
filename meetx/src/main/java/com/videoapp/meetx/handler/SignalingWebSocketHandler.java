package com.videoapp.meetx.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoapp.meetx.model.Participant;
import com.videoapp.meetx.model.SignalingMessage;
import com.videoapp.meetx.service.ParticipantService;
import com.videoapp.meetx.service.RoomService;
import com.videoapp.meetx.service.TokenService;
import com.videoapp.meetx.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final ParticipantService participantService;
    private final TokenService tokenService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            SignalingMessage signalingMessage = objectMapper.readValue(
                    message.getPayload(),
                    SignalingMessage.class);

            log.debug("Received message type: {} from session: {}",
                    signalingMessage.getType(), session.getId());

            handleMessage(session, signalingMessage);

        } catch (Exception e) {
            log.error("Error processing message: ", e);
            sendError(session, Constants.ERR_INVALID_MESSAGE);
        }
    }

    private void handleMessage(WebSocketSession session, SignalingMessage message) {
        try {
            String type = message.getType();

            if (type == null) {
                sendError(session, Constants.ERR_INVALID_MESSAGE);
                return;
            }

            switch (type.toUpperCase()) {
                case "JOIN" -> handleJoin(session, message);
                case "OFFER" -> handleOffer(session, message);
                case "ANSWER" -> handleAnswer(session, message);
                case "ICE_CANDIDATE", "ICE-CANDIDATE" -> handleIceCandidate(session, message);
                case "LEAVE" -> handleLeave(session, message);
                default -> {
                    log.warn("Unknown message type: {}", type);
                    sendError(session, "Unknown message type: " + type);
                }
            }
        } catch (Exception e) {
            log.error("Error handling message: ", e);
            sendError(session, e.getMessage());
        }
    }

    private void handleJoin(WebSocketSession session, SignalingMessage message) {
        String roomId = message.getRoomId();
        String token = message.getToken();

        if (roomId == null || token == null) {
            sendError(session, "Room ID and token are required");
            return;
        }

        try {
            // Validate token
            tokenService.validateToken(roomId, token);

            // Create participant
            Participant participant = participantService.createParticipant(roomId, session);

            // Add to room
            roomService.addParticipantToRoom(roomId, participant.getId());

            // Send ready message to the joining participant
            SignalingMessage readyMsg = SignalingMessage.ready(participant.getId());
            readyMsg.setRoomId(roomId);
            sendMessage(session, readyMsg);

            log.info("Participant {} joined room {}", participant.getId(), roomId);
            List<Participant> roomParticipants = participantService.getRoomParticipants(roomId);
            log.info("Found {} participants in room {}", roomParticipants.size(), roomId); // ADD THIS

            log.info("Room {} now has {} total participants", roomId, roomParticipants.size());
            for (Participant existingParticipant : roomParticipants) {
                log.info("Checking participant {}: isConnected={}",
                        existingParticipant.getId(), existingParticipant.isConnected()); // ADD THIS
                if (!existingParticipant.getId().equals(participant.getId()) && existingParticipant.isConnected()) {
                    SignalingMessage joinNotification = SignalingMessage.participantJoined(roomId, participant.getId());
                    sendMessage(existingParticipant.getSession(), joinNotification);
                    log.info("✓ Notified participant {} about new joiner {}", existingParticipant.getId(),
                            participant.getId());
                }
            }

        } catch (Exception e) {
            log.error("Error handling join: ", e);
            sendError(session, e.getMessage());
        }
    }

    private void handleOffer(WebSocketSession session, SignalingMessage message) {
        Participant sender = participantService.getParticipantBySession(session.getId());

        if (sender == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        String targetId = message.getTargetParticipantId();
        if (targetId == null) {
            sendError(session, "Target participant ID is required");
            return;
        }

        Participant target = participantService.getParticipant(targetId);
        if (target == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        // Forward offer to target
        message.setParticipantId(sender.getId());
        sendMessage(target.getSession(), message);

        participantService.updateActivity(sender.getId());
        log.debug("Forwarded offer from {} to {}", sender.getId(), targetId);
    }

    private void handleAnswer(WebSocketSession session, SignalingMessage message) {
        Participant sender = participantService.getParticipantBySession(session.getId());

        if (sender == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        String targetId = message.getTargetParticipantId();
        if (targetId == null) {
            sendError(session, "Target participant ID is required");
            return;
        }

        Participant target = participantService.getParticipant(targetId);
        if (target == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        // Forward answer to target
        message.setParticipantId(sender.getId());
        sendMessage(target.getSession(), message);

        participantService.updateActivity(sender.getId());
        log.debug("Forwarded answer from {} to {}", sender.getId(), targetId);
    }

    private void handleIceCandidate(WebSocketSession session, SignalingMessage message) {
        Participant sender = participantService.getParticipantBySession(session.getId());

        if (sender == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        String targetId = message.getTargetParticipantId();
        if (targetId == null) {
            sendError(session, "Target participant ID is required");
            return;
        }

        Participant target = participantService.getParticipant(targetId);
        if (target == null) {
            sendError(session, Constants.ERR_PARTICIPANT_NOT_FOUND);
            return;
        }

        // Forward ICE candidate to target
        message.setParticipantId(sender.getId());
        sendMessage(target.getSession(), message);

        participantService.updateActivity(sender.getId());
        log.debug("Forwarded ICE candidate from {} to {}", sender.getId(), targetId);
    }

    private void handleLeave(WebSocketSession session, SignalingMessage message) {
        Participant participant = participantService.getParticipantBySession(session.getId());

        if (participant != null) {
            String roomId = participant.getRoomId();
            String participantId = participant.getId();

            // Notify others BEFORE removing
            notifyOthersInRoom(roomId, participantId,
                    SignalingMessage.participantLeft(roomId, participantId));

            // Cleanup
            roomService.removeParticipantFromRoom(roomId, participantId);
            participantService.removeParticipant(participantId);

            log.info("Participant {} left room {}", participantId, roomId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket connection closed: {} with status: {}", session.getId(), status);

        Participant participant = participantService.getParticipantBySession(session.getId());
        if (participant != null) {
            String roomId = participant.getRoomId();
            String participantId = participant.getId();

            // Notify others BEFORE removing
            notifyOthersInRoom(roomId, participantId,
                    SignalingMessage.participantLeft(roomId, participantId));

            // Cleanup
            roomService.removeParticipantFromRoom(roomId, participantId);
            participantService.removeParticipant(participantId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error for session {}: ", session.getId(), exception);

        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error("Error closing session: ", e);
        }
    }

    private void sendMessage(WebSocketSession session, SignalingMessage message) {
        try {
            if (session.isOpen()) {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
                log.debug("Sent message type {} to session {}", message.getType(), session.getId());
            } else {
                log.warn("Cannot send message - session {} is closed", session.getId());
            }
        } catch (IOException e) {
            log.error("Error sending message: ", e);
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) {
        sendMessage(session, SignalingMessage.error(errorMessage));
    }

    private void notifyOthersInRoom(String roomId, String senderId, SignalingMessage message) {
        List<Participant> participants = participantService.getRoomParticipants(roomId);

        int notifiedCount = 0;
        for (Participant p : participants) {
            if (!p.getId().equals(senderId) && p.isConnected()) {
                sendMessage(p.getSession(), message);
                notifiedCount++;
            }
        }

        log.debug("Notified {} participants in room {} about message type {}",
                notifiedCount, roomId, message.getType());
    }
}