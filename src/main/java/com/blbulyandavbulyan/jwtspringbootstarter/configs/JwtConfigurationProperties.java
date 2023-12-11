package com.blbulyandavbulyan.jwtspringbootstarter.configs;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Setter
@Getter
public class JwtConfigurationProperties {
    private Duration lifetime = Duration.ofMinutes(10);
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
}