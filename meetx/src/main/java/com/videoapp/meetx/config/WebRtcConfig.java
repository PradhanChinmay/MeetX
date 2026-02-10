package com.videoapp.meetx.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "webrtc")
@Data 
public class WebRtcConfig {

    private StunConfig stun;
    private TurnConfig turn;

    @Data
    public static class StunConfig {
        private List<Map<String, String>> servers;
    }

    @Data
    public static class TurnConfig {
        private boolean enabled;
        private List<Map<String, String>> servers;
    }
}
