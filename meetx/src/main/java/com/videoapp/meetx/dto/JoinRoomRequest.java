package com.videoapp.meetx.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRoomRequest {

    @NotBlank(message = "Room ID is required")
    private String roomId;
}
