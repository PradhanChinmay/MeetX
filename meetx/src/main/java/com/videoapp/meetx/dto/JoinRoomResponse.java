package com.videoapp.meetx.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinRoomResponse {

    private String roomId;
    private String roomName;
    private String token;
    private String participantId;
    private int currentParticipants;
    private List<Map<String, String>> iceServers;
}
