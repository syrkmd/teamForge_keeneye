package org.yvl.teamforge.security.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yvl.teamforge.security.handler.JwtAuthenticationEntryPoint;
import org.yvl.teamforge.security.jwt.service.JwtService;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {


    @Mock
    private JwtService jwtService;

    @Mock
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Test
    void validTokenCreatesAuthentication() throws ServletException, IOException {
        Claims claims = mock(Claims.class);

        String token = "test-token";

        when(jwtService.getClaims(token)).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(123L);
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertInstanceOf(UserPrincipal.class, authentication.getPrincipal());

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        assertEquals(123L, principal.getUser().getId());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_ADMIN")));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void invalidTokenReturnsUnauthorized() throws ServletException, IOException {
        String token = "invalid-token";

        when(jwtService.getClaims(token))
                .thenThrow(new JwtException("Invalid token"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                any(BadCredentialsException.class)
        );
    }

    @Test
    void tokenWithoutUserIdReturnsUnauthorized() throws ServletException, IOException {
        Claims claims = mock(Claims.class);

        String token = "test-token";

        when(jwtService.getClaims(token)).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                any(BadCredentialsException.class)
        );
    }

    @Test
    void tokenWithoutRoleReturnsUnauthorized() throws ServletException, IOException {
        Claims claims = mock(Claims.class);

        String token = "test-token";

        when(jwtService.getClaims(token)).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(123L);
        when(claims.get("role", String.class)).thenReturn(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                any(BadCredentialsException.class)
        );
    }

    @Test
    void invalidRoleReturnsUnauthorized() throws ServletException, IOException {
        Claims claims = mock(Claims.class);

        String token = "test-token";

        when(jwtService.getClaims(token)).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(123L);
        when(claims.get("role", String.class)).thenReturn("INVALID");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                any(BadCredentialsException.class)
        );
    }

    @Test
    void expiredTokenReturnsUnauthorized() throws ServletException, IOException {
        String token = "test-token";

        when(jwtService.getClaims(token))
                .thenThrow(ExpiredJwtException.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint).commence(
                eq(request),
                eq(response),
                any(BadCredentialsException.class)
        );
    }
}