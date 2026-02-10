package com.videoapp.meetx.util;

public class Constants {

    /* WebSocket message types */ 
    public static final String MSG_TYPE_OFFER = "offer";
    public static final String MSG_TYPE_ANSWER = "answer";
    public static final String MSG_TYPE_ICE_CANDIDATE = "ice-candidate";
    public static final String MSG_TYPE_JOIN = "join";
    public static final String MSG_TYPE_LEAVE = "leave";
    public static final String MSG_TYPE_ERROR = "error";
    public static final String MSG_TYPE_PARTICIPANT_JOINED = "participant-joined";
    public static final String MSG_TYPE_PARTICIPANT_LEFT = "participant-left";
    public static final String MSG_TYPE_READY = "ready";
    
    /* Error messages */ 
    public static final String ERR_ROOM_NOT_FOUND = "Room not found";
    public static final String ERR_ROOM_FULL = "Room is full";
    public static final String ERR_INVALID_TOKEN = "Invalid or expired token";
    public static final String ERR_PARTICIPANT_NOT_FOUND = "Participant not found";
    public static final String ERR_INVALID_MESSAGE = "Invalid message format";
    public static final String ERR_INTERNAL_SERVER = "Internal server error";
    
    /* Token related */
    public static final int TOKEN_LENGTH = 32;
    
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

}
