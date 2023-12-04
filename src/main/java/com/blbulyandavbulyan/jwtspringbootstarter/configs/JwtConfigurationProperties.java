package com.blbulyandavbulyan.jwtspringbootstarter.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt-spring-boot-starter")
public class JwtConfigurationProperties {
    private Duration lifetime;

    public Duration getLifetime() {
        return lifetime;
    }

    public void setLifetime(Duration lifetime) {
        this.lifetime = lifetime;
    }
}