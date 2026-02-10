package com.videoapp.meetx.exception;

public class RoomFullException extends RuntimeException {

    public RoomFullException(String message) {
        super(message);
    }
}
