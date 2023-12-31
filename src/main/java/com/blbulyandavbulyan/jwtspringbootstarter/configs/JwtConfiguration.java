package com.blbulyandavbulyan.jwtspringbootstarter.configs;

import com.blbulyandavbulyan.jwtspringbootstarter.filters.JwtRequestFilter;
import com.blbulyandavbulyan.jwtspringbootstarter.services.TokenService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {
    @Bean
    public TokenService tokenService(JwtConfigurationProperties jwtConfigurationProperties){
        return new TokenService(jwtConfigurationProperties);
    }
    @Bean
    public JwtRequestFilter jwtRequestFilter(TokenService tokenService){
        return new JwtRequestFilter(tokenService);
    }
    @Bean
    @ConfigurationProperties(prefix = "jwt-spring-boot-starter")
    public JwtConfigurationProperties jwtConfigurationProperties(){
        return new JwtConfigurationProperties();
    }
}
