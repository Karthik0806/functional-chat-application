package com.karthik.functionalchatapplication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // 64-char secret satisfies HS256 minimum
    private static final String SECRET =
            "thisisaverylongsecretkeyusedfortestingpurposesonly1234567890abcdef";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3_600_000L); // 1 hour
        jwtService.init();
    }

    // -------------------------------------------------------- generateToken()
    @Nested
    @DisplayName("generateToken()")
    class GenerateToken {

        @Test
        @DisplayName("returns a non-blank JWT string")
        void returnsNonBlankToken() {
            String token = jwtService.generateToken("karthik");
            assertThat(token).isNotBlank().contains(".");
        }

        @Test
        @DisplayName("embeds username as subject")
        void subjectIsUsername() {
            String token = jwtService.generateToken("karthik");
            assertThat(jwtService.extractUsername(token)).isEqualTo("karthik");
        }

        @Test
        @DisplayName("token has 'access' type claim")
        void accessTypeClaim() {
            String token = jwtService.generateToken("karthik");
            assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        }

        @Test
        @DisplayName("expiry is approximately now + expiration millis")
        void expirationIsCorrect() {
            long before = System.currentTimeMillis();
            String token = jwtService.generateToken("karthik");
            Date expiry = jwtService.extractExpiration(token);
            long after = System.currentTimeMillis();

            assertThat(expiry.getTime()).isBetween(before + 3_590_000L, after + 3_610_000L);
        }
    }

    // ---------------------------------------------------------- isValid()
    @Nested
    @DisplayName("isValid()")
    class IsValid {

        @Test
        @DisplayName("returns true for a freshly generated access token")
        void validToken() {
            assertThat(jwtService.isValid(jwtService.generateToken("karthik"))).isTrue();
        }

        @Test
        @DisplayName("returns false for a garbled token")
        void garbledToken() {
            assertThat(jwtService.isValid("not.a.real.jwt")).isFalse();
        }

        @Test
        @DisplayName("returns false for an expired token")
        void expiredToken() {
            ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // already expired
            jwtService.init();
            String expired = jwtService.generateToken("karthik");
            assertThat(jwtService.isValid(expired)).isFalse();
        }

        @Test
        @DisplayName("returns false for token signed with a different secret")
        void wrongSecret() {
            JwtService other = new JwtService();
            ReflectionTestUtils.setField(other, "secret",
                    "completelydifferentsecretkey999888777666555444333222111000aabbccdd");
            ReflectionTestUtils.setField(other, "expiration", 3_600_000L);
            other.init();
            String foreignToken = other.generateToken("karthik");
            assertThat(jwtService.isValid(foreignToken)).isFalse();
        }
    }

    // ------------------------------------------------- generateRefreshToken()
    @Nested
    @DisplayName("generateRefreshToken()")
    class GenerateRefreshToken {

        @Test
        @DisplayName("returns non-blank UUID-like string")
        void nonBlank() {
            assertThat(jwtService.generateRefreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("each call returns a unique value")
        void uniquePerCall() {
            Set<String> tokens = new HashSet<>();
            for (int i = 0; i < 20; i++) tokens.add(jwtService.generateRefreshToken());
            assertThat(tokens).hasSize(20);
        }
    }
}
