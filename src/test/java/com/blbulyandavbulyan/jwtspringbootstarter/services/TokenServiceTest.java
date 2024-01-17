package com.blbulyandavbulyan.jwtspringbootstarter.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.blbulyandavbulyan.jwtspringbootstarter.configs.JwtConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Mock
    private JwtConfigurationProperties jwtConfigurationProperties;

    @BeforeEach
    public void setUp() {
        when(jwtConfigurationProperties.getSignatureAlgorithm()).thenReturn(signatureAlgorithm);
    }
    @Test
    void generateToken() {
        Duration expectedLifetime = Duration.ofMinutes(10);
        when(jwtConfigurationProperties.getLifetime()).thenReturn(expectedLifetime);
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class);
             MockedStatic<Keys> keysMockedStatic = Mockito.mockStatic(Keys.class)) {
            String expectedJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            String expectedName = "testuser";
            var expectedRoles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_COMMENTER"));
            Key keyMock = Mockito.mock(SecretKey.class);
            JwtBuilder mockJwtBuilder = Mockito.mock(JwtBuilder.class);
            JwtParserBuilder mockParserBuilder = mock(JwtParserBuilder.class);
            jwtsMockedStatic.when(Jwts::parserBuilder).thenReturn(mockParserBuilder);
            when(mockParserBuilder.setSigningKey(keyMock)).thenAnswer(InvocationOnMock::getMock);
            keysMockedStatic.when(() -> Keys.secretKeyFor(signatureAlgorithm)).thenReturn(keyMock);
            when(mockJwtBuilder.setClaims(anyMap())).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setSubject(anyString())).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setIssuedAt(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.setExpiration(any(Date.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.signWith(any(SecretKey.class))).thenAnswer(InvocationOnMock::getMock);
            when(mockJwtBuilder.compact()).thenReturn(expectedJwtToken);
            jwtsMockedStatic.when(Jwts::builder).thenReturn(mockJwtBuilder);
            var underTest = new TokenService(jwtConfigurationProperties);
            String actualToken = assertDoesNotThrow(()-> underTest.generateToken(expectedName, expectedRoles));
            assertSame(expectedJwtToken, actualToken);
            HashMap<String, Object> expectedClaims = new HashMap<>();
            expectedClaims.put("roles", expectedRoles.stream().map(SimpleGrantedAuthority::getAuthority).toList());
            ArgumentCaptor<Date> issuedAtArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            ArgumentCaptor<Date> expirationArgumentCaptor = ArgumentCaptor.forClass(Date.class);
            verify(mockJwtBuilder, times(1)).setIssuedAt(issuedAtArgumentCaptor.capture());
            verify(mockJwtBuilder, times(1)).setExpiration(expirationArgumentCaptor.capture());
            Date issuedAt = issuedAtArgumentCaptor.getValue();
            Date expiration = expirationArgumentCaptor.getValue();
            assertTrue(issuedAt.before(new Date()));
            assertEquals(expiration.getTime() - issuedAt.getTime(), expectedLifetime.toMillis());
            verify(mockJwtBuilder, times(1)).setClaims(expectedClaims);
            verify(mockJwtBuilder, times(1)).setSubject(expectedName);
            verify(mockJwtBuilder, times(1)).signWith(keyMock);
        }
    }

    private void executeWithMockClaims(Consumer<Claims> claimsConsumer, String token) {
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class);
             MockedStatic<Keys> keysMockedStatic = Mockito.mockStatic(Keys.class)) {
            JwtParserBuilder mockParserBuilder = mock(JwtParserBuilder.class);
            jwtsMockedStatic.when(Jwts::parserBuilder).thenReturn(mockParserBuilder);
            Key keyMock = Mockito.mock(SecretKey.class);
            when(mockParserBuilder.setSigningKey(keyMock)).thenAnswer(InvocationOnMock::getMock);
            JwtParser mockJwtParser = mock(JwtParser.class);
            when(mockParserBuilder.build()).thenReturn(mockJwtParser);
            keysMockedStatic.when(() -> Keys.secretKeyFor(signatureAlgorithm)).thenReturn(keyMock);
            Jws<Claims> jwsMock = mock(Jws.class);
            when(mockJwtParser.parseClaimsJws(token)).thenReturn(jwsMock);
            Claims mockClaims = mock(Claims.class);
            when(jwsMock.getBody()).thenReturn(mockClaims);
            claimsConsumer.accept(mockClaims);
        }
    }

    @Test
    void getUserName() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        executeWithMockClaims((claims) -> {
            String expectedUsername = "testusername";
            when(claims.getSubject()).thenReturn(expectedUsername);
            TokenService underTest = new TokenService(jwtConfigurationProperties);
            String actualUsername = assertDoesNotThrow(() -> underTest.getUserName(token));
            assertSame(expectedUsername, actualUsername);
        }, token);
    }
    @Test
    void getRoles(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        executeWithMockClaims((claims) -> {
            List<String> expectedRoles = List.of();
            when(claims.get("roles", List.class)).thenReturn(expectedRoles);
            TokenService underTest = new TokenService(jwtConfigurationProperties);
            List<String> actualRoles = assertDoesNotThrow(() -> underTest.getRoles(token));
            assertSame(expectedRoles, actualRoles);
        }, token);
    }
}