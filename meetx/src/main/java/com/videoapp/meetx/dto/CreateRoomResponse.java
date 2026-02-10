package com.videoapp.meetx.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRoomResponse {

    private String roomId;
    private String roomName;
    private String token;
    private String createdAt;
    private String expiresAt;
    private List<Map<String, String>> iceServers;
}
