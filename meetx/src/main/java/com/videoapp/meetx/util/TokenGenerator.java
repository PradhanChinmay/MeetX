package com.videoapp.meetx.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateToken(int length) {

        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String generateRoomId() {
        return generateToken(16);
    }

    public static String generateParticipantToken() {
        return generateToken(Constants.TOKEN_LENGTH);
    }
    
    private TokenGenerator() {
        throw new IllegalStateException("Utility class");
    }

}
