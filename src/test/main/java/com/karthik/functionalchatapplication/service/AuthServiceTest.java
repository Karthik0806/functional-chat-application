package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.AuthProvider;
import com.karthik.functionalchatapplication.dto.AuthResponse;
import com.karthik.functionalchatapplication.dto.LoginRequest;
import com.karthik.functionalchatapplication.dto.RefreshRequest;
import com.karthik.functionalchatapplication.dto.RegisterRequest;
import com.karthik.functionalchatapplication.entity.RefreshToken;
import com.karthik.functionalchatapplication.entity.User;
import com.karthik.functionalchatapplication.exception.UserAlreadyExistsException;
import com.karthik.functionalchatapplication.repo.RefreshTokenRepo;
import com.karthik.functionalchatapplication.repo.UserRepo;
import com.karthik.functionalchatapplication.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock UserRepo userRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RefreshTokenRepo refreshTokenRepo;

    @InjectMocks AuthService authService;

    private User localUser;
    private User googleUser;

    @BeforeEach
    void setUp() {
        localUser = User.builder()
                .id(1L).username("karthik").password("encoded").provider(AuthProvider.LOCAL).build();
        googleUser = User.builder()
                .id(2L).username("karthik@gmail.com").password("").provider(AuthProvider.GOOGLE).build();
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("returns tokens on valid LOCAL credentials")
        void successfulLogin() {
            var req = loginRequest("karthik", "Pass@123");
            when(userRepo.findByUsername("karthik")).thenReturn(Optional.of(localUser));
            when(jwtService.generateToken("karthik")).thenReturn("access-token");
            when(jwtService.generateRefreshToken()).thenReturn("refresh-token");

            AuthResponse response = authService.login(req);

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            verify(refreshTokenRepo).deleteByUsername("karthik");
            verify(refreshTokenRepo).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("throws BadCredentialsException for unknown user")
        void unknownUser() {
            when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> authService.login(loginRequest("unknown", "pass")))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("throws BadCredentialsException for GOOGLE user attempting password login")
        void googleUserCannotUsePasswordLogin() {
            when(userRepo.findByUsername("karthik@gmail.com")).thenReturn(Optional.of(googleUser));
            assertThatThrownBy(() -> authService.login(loginRequest("karthik@gmail.com", "pass")))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Google");
        }

        @Test
        @DisplayName("delegates credential check to AuthenticationManager")
        void delegatesToAuthManager() {
            when(userRepo.findByUsername("karthik")).thenReturn(Optional.of(localUser));
            when(jwtService.generateToken(any())).thenReturn("tok");
            when(jwtService.generateRefreshToken()).thenReturn("ref");

            authService.login(loginRequest("karthik", "Pass@123"));

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("saves new LOCAL user successfully")
        void registersNewUser() {
            var req = registerRequest("newuser", "Secure@123");
            when(userRepo.findByUsername("newuser")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("Secure@123")).thenReturn("encoded");

            authService.register(req);

            verify(userRepo).save(argThat(u ->
                    u.getUsername().equals("newuser")
                            && u.getProvider() == AuthProvider.LOCAL
                            && u.getPassword().equals("encoded")));
        }

        @Test
        @DisplayName("throws UserAlreadyExistsException when username taken")
        void duplicateUsername() {
            when(userRepo.findByUsername("karthik")).thenReturn(Optional.of(localUser));
            assertThatThrownBy(() -> authService.register(registerRequest("karthik", "Pass@123")))
                    .isInstanceOf(UserAlreadyExistsException.class);
        }
    }

    @Nested
    @DisplayName("refreshToken()")
    class RefreshTokenTests {

        @Test
        @DisplayName("rotates tokens and returns new pair")
        void rotatesTokens() {
            var stored = storedRefreshToken("old-refresh", "karthik", Instant.now().plusSeconds(3600));
            when(refreshTokenRepo.findByToken("old-refresh")).thenReturn(Optional.of(stored));
            when(jwtService.generateToken("karthik")).thenReturn("new-access");
            when(jwtService.generateRefreshToken()).thenReturn("new-refresh");

            var req = new RefreshRequest();
            req.setRefreshToken("old-refresh");
            AuthResponse response = authService.refreshToken(req);

            assertThat(response.getAccessToken()).isEqualTo("new-access");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
            verify(refreshTokenRepo).delete(stored);
            verify(refreshTokenRepo).save(argThat(t -> t.getToken().equals("new-refresh")));
        }

        @Test
        @DisplayName("throws RuntimeException for unknown refresh token")
        void invalidToken() {
            when(refreshTokenRepo.findByToken("bad-token")).thenReturn(Optional.empty());
            var req = new RefreshRequest();
            req.setRefreshToken("bad-token");
            assertThatThrownBy(() -> authService.refreshToken(req))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("throws RuntimeException for expired refresh token")
        void expiredToken() {
            var expired = storedRefreshToken("expired", "karthik", Instant.now().minusSeconds(1));
            when(refreshTokenRepo.findByToken("expired")).thenReturn(Optional.of(expired));
            var req = new RefreshRequest();
            req.setRefreshToken("expired");
            assertThatThrownBy(() -> authService.refreshToken(req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("expired");
        }
    }

    @Nested
    @DisplayName("logout()")
    class Logout {

        @Test
        @DisplayName("deletes existing refresh token")
        void deletesToken() {
            var stored = storedRefreshToken("tok", "karthik", Instant.now().plusSeconds(100));
            when(refreshTokenRepo.findByToken("tok")).thenReturn(Optional.of(stored));
            authService.logout("tok");
            verify(refreshTokenRepo).delete(stored);
        }

        @Test
        @DisplayName("does nothing for unknown token (graceful)")
        void unknownTokenIsNoop() {
            when(refreshTokenRepo.findByToken("ghost")).thenReturn(Optional.empty());
            assertThatCode(() -> authService.logout("ghost")).doesNotThrowAnyException();
            verify(refreshTokenRepo, never()).delete(any());
        }
    }

    private LoginRequest loginRequest(String username, String password) {
        var r = new LoginRequest(); r.setUsername(username); r.setPassword(password); return r;
    }

    private RegisterRequest registerRequest(String username, String password) {
        var r = new RegisterRequest(); r.setUsername(username); r.setPassword(password); return r;
    }

    private RefreshToken storedRefreshToken(String token, String username, Instant expiry) {
        return RefreshToken.builder().token(token).username(username).expiryDate(expiry).build();
    }
}