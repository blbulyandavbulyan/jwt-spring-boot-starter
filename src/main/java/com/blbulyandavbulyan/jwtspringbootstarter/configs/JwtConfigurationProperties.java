package com.blbulyandavbulyan.jwtspringbootstarter.configs;
import java.time.Duration;

public class JwtConfigurationProperties {
    private Duration lifetime;

    public Duration getLifetime() {
        return lifetime;
    }

    public void setLifetime(Duration lifetime) {
        this.lifetime = lifetime;
    }
}