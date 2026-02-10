package com.videoapp.meetx.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.videoapp.meetx.config.WebRtcConfig;
import com.videoapp.meetx.dto.CreateRoomRequest;
import com.videoapp.meetx.dto.CreateRoomResponse;
import com.videoapp.meetx.dto.JoinRoomRequest;
import com.videoapp.meetx.dto.JoinRoomResponse;
import com.videoapp.meetx.model.Room;
import com.videoapp.meetx.service.RoomService;
import com.videoapp.meetx.service.TokenService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {


    private final RoomService roomService;
    private final TokenService tokenService;
    private final WebRtcConfig webRtcConfig;

    // @Value("${webrtc.stun.servers}")
    // private List<Map<String, String>> stunServers;

    // @Value("${webrtc.turn.enabled}")
    // private boolean turnEnabled;

    // @Value("${webrtc.turn.servers}")
    // private List<Map<String, String>> turnServers;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {
        
        log.info("Creating room with name: {}", request.getName());
        
        Room room = roomService.createRoom(request.getName());
        
        CreateRoomResponse response = CreateRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .token(room.getCreatorToken())
                .createdAt(room.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .expiresAt(room.getExpiresAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .iceServers(getIceServers())
                .build();
        
        log.info("Room created successfully: {}", room.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(
            @Valid @RequestBody JoinRoomRequest request) {
        
        log.info("Joining room: {}", request.getRoomId());
        
        Room room = roomService.getRoom(request.getRoomId());

        // Check if room is full BEFORE generating token
        if (room.isFull()) {
            throw new com.videoapp.meetx.exception.RoomFullException("Room is full");
        }
        
        // Generate a NEW token for this join request (not the creator token)
        String token = tokenService.generateRoomToken(room.getId());
        
        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .token(token)
                .participantId(null) // Will be assigned on WebSocket connection
                .currentParticipants(room.getParticipantIds().size())
                .iceServers(getIceServers())
                .build();
        
        log.info("Join token generated for room: {}", room.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomInfo(@PathVariable String roomId) {
        log.info("Getting room info: {}", roomId);
        
        Room room = roomService.getRoom(roomId);
        
        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("roomId", room.getId());
        roomInfo.put("roomName", room.getName());
        roomInfo.put("createdAt", room.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        roomInfo.put("expiresAt", room.getExpiresAt().format(DateTimeFormatter.ISO_DATE_TIME));
        roomInfo.put("currentParticipants", room.getParticipantIds().size());
        roomInfo.put("maxParticipants", room.getMaxParticipants());
        roomInfo.put("isFull", room.isFull());
        
        return ResponseEntity.ok(roomInfo);
    }

   private List<Map<String, String>> getIceServers() {
        List<Map<String, String>> iceServers = new ArrayList<>(webRtcConfig.getStun().getServers());
        
        if (webRtcConfig.getTurn().isEnabled() && webRtcConfig.getTurn().getServers() != null) {
            iceServers.addAll(webRtcConfig.getTurn().getServers());
        }
        return iceServers;
    }
}
