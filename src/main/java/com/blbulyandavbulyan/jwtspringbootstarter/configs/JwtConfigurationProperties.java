package com.blbulyandavbulyan.jwtspringbootstarter.configs;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "jwt-spring-boot-starter")
@Setter
@Getter
public class JwtConfigurationProperties {
    private Duration lifetime = Duration.ofMinutes(10);
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
}