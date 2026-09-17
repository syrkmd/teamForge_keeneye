package org.yvl.notificationservice.security.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yvl.notificationservice.config.JwtProperties;
import org.yvl.notificationservice.security.handler.JwtAuthenticationEntryPoint;
import org.yvl.notificationservice.security.jwt.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            Jws<Claims> jws = jwtService.parseToken(token);

            Claims claims = jws.getPayload();
            String kid = jws.getHeader().getKeyId();

            if (!jwtProperties.getKid().equals(kid)) {
                throw new JwtException("Invalid JWT key id");
            }

            Long userId = claims.get("userId", Long.class);

            if (userId == null) {
                throw new JwtException("JWT does not contain userId");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("JWT expired", e)
            );
        } catch (JwtException e) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("JWT error", e)
            );
        }

    }
}
