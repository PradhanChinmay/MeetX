package com.videoapp.meetx.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalingMessage {

    private String type;
    private String roomId;
    private String token;
    private String participantId;
    private String targetParticipantId;
    private Object payload;
    private String error;
    private Long timestamp;

    public static SignalingMessage error(String errorMessage) {
        return SignalingMessage.builder().type(MessageType.ERROR.name().toLowerCase())
        .error(errorMessage).timestamp(System.currentTimeMillis()).build();
    }
    
    public static SignalingMessage ready(String participantId) {
        return SignalingMessage.builder()
                .type(MessageType.READY.name().toLowerCase())
                .participantId(participantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static SignalingMessage participantJoined(String roomId, String participantId) {
        return SignalingMessage.builder()
                .type(MessageType.PARTICIPANT_JOINED.name().toLowerCase())
                .roomId(roomId)
                .participantId(participantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static SignalingMessage participantLeft(String roomId, String participantId) {
        return SignalingMessage.builder()
                .type(MessageType.PARTICIPANT_LEFT.name().toLowerCase())
                .roomId(roomId)
                .participantId(participantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
