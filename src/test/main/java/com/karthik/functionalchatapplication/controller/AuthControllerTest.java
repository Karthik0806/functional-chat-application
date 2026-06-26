package com.karthik.functionalchatapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik.functionalchatapplication.dto.AuthResponse;
import com.karthik.functionalchatapplication.dto.LoginRequest;
import com.karthik.functionalchatapplication.dto.RefreshRequest;
import com.karthik.functionalchatapplication.dto.RegisterRequest;
import com.karthik.functionalchatapplication.security.AuthController;
import com.karthik.functionalchatapplication.security.JwtFilter;
import com.karthik.functionalchatapplication.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("200 with tokens on valid credentials")
        @WithMockUser
        void successfulLogin() throws Exception {
            var req = new LoginRequest();
            req.setUsername("karthik");
            req.setPassword("Pass@123");

            when(authService.login(any())).thenReturn(
                    AuthResponse.builder().accessToken("acc").refreshToken("ref").build());

            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("acc"))
                    .andExpect(jsonPath("$.refreshToken").value("ref"));
        }

        @Test
        @DisplayName("401 on bad credentials")
        @WithMockUser
        void badCredentials() throws Exception {
            var req = new LoginRequest();
            req.setUsername("karthik");
            req.setPassword("Wrong@123");

            when(authService.login(any())).thenThrow(new BadCredentialsException("bad"));

            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("400 when username is blank (validation)")
        @WithMockUser
        void blankUsername() throws Exception {
            var req = new LoginRequest();
            req.setUsername("");
            req.setPassword("Pass@123");

            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }

    @Nested
    @DisplayName("POST /auth/register")
    class Register {

        @Test
        @DisplayName("201 on successful registration")
        @WithMockUser
        void successfulRegister() throws Exception {
            var req = new RegisterRequest();
            req.setUsername("newuser1");
            req.setPassword("Secure@123");

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("User registered successfully"));

            verify(authService).register(any());
        }

        @Test
        @DisplayName("400 when password fails pattern constraint")
        @WithMockUser
        void weakPassword() throws Exception {
            var req = new RegisterRequest();
            req.setUsername("newuser1");
            req.setPassword("simple");   // no uppercase, no special char

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh")
    class Refresh {

        @Test
        @DisplayName("200 with new token pair on valid refresh token")
        @WithMockUser
        void refreshesToken() throws Exception {
            var req = new RefreshRequest();
            req.setRefreshToken("old-refresh");

            when(authService.refreshToken(any())).thenReturn(
                    AuthResponse.builder().accessToken("new-acc").refreshToken("new-ref").build());

            mockMvc.perform(post("/auth/refresh")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("new-acc"));
        }
    }

    @Nested
    @DisplayName("POST /auth/logout")
    class Logout {

        @Test
        @DisplayName("200 with success message")
        @WithMockUser
        void logout() throws Exception {
            var req = new RefreshRequest();
            req.setRefreshToken("tok");

            mockMvc.perform(post("/auth/logout")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logged out successfully"));

            verify(authService).logout("tok");
        }
    }
}
