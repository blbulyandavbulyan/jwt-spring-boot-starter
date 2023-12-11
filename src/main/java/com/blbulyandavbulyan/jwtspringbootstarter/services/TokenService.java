package com.blbulyandavbulyan.jwtspringbootstarter.services;

import com.blbulyandavbulyan.jwtspringbootstarter.configs.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.*;

public class TokenService {
    /**
     * Время жизни jwt токена
     */
    private final JwtConfigurationProperties jwtConfigurationProperties;
    /**
     * Ключ для подписи jwt токена
     */
    private final Key secretKey;
    /**
     * Парсер, для парсинга jwt токена
     */
    private final JwtParser parser;

    /**
     * Создаёт экземпляр сервиса
     * @param jwtConfigurationProperties класс, содержащий конфигурационные свойства для jwt
     */
    public TokenService(JwtConfigurationProperties jwtConfigurationProperties) {
        this.jwtConfigurationProperties = jwtConfigurationProperties;
        //задаём ключ подписи
        secretKey = Keys.secretKeyFor(jwtConfigurationProperties.getSignatureAlgorithm()); //or HS384 or HS512
        //создаём парсер
        parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * Метод генерирует jwt токен
     * @param name имя пользователя
     * @param authorities права пользователя
     * @return полученный jwt токен
     */
    public String generateToken(String name, Collection<? extends GrantedAuthority> authorities){
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", rolesList);//добавляем роли
        Date issuedDate = new Date();//время создания токена
        Date expiredDate = new Date(issuedDate.getTime() + jwtConfigurationProperties.getLifetime().toMillis());//время истечения токена
        return Jwts.builder().setClaims(claims)
                .setSubject(name)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(secretKey).compact();
    }

    private Claims getAllClaimsFromToken(String token){
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * Получаем имя пользователя из jwt токена
     * @param token jwt токен
     * @return имя пользователя, которое было в jwt токене
     */
    public String getUserName(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Получаем список ролей из jwt токена
     * @param token jwt токен, из которого будут получены роли
     * @return список ролей, которые были в jwt токене
     */
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }
}