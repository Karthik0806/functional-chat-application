package com.karthik.functionalchatapplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain filterChain;
    @Mock UserDetails userDetails;

    @InjectMocks JwtFilter jwtFilter;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("No / missing Authorization header")
    class NoHeader {

        @Test
        @DisplayName("passes through to filter chain without touching SecurityContext")
        void noHeader() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("passes through when Authorization header does not start with Bearer")
        void nonBearerHeader() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");
            jwtFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Valid Bearer token")
    class ValidToken {

        @Test
        @DisplayName("sets authentication in SecurityContext and continues chain")
        void setsAuthentication() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
            when(jwtService.isValid("valid.jwt.token")).thenReturn(true);
            when(jwtService.extractUsername("valid.jwt.token")).thenReturn("karthik");
            when(userDetailsService.loadUserByUsername("karthik")).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userDetails);
        }
    }

    @Nested
    @DisplayName("Invalid Bearer token")
    class InvalidToken {

        @Test
        @DisplayName("returns 401 and does NOT continue the filter chain")
        void returns401() throws Exception {
            var writer = new StringWriter();
            when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
            when(jwtService.isValid("bad.token")).thenReturn(false);
            when(response.getWriter()).thenReturn(new PrintWriter(writer));

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(filterChain, never()).doFilter(any(), any());
            assertThat(writer.toString()).contains("Unauthorized");
        }

        @Test
        @DisplayName("clears SecurityContext on invalid token")
        void clearsSecurityContext() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer expired");
            when(jwtService.isValid("expired")).thenReturn(false);
            when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}
