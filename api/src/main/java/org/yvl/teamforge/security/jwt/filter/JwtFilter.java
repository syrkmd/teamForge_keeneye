package org.yvl.teamforge.security.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yvl.teamforge.security.handler.JwtAuthenticationEntryPoint;
import org.yvl.teamforge.security.jwt.service.JwtService;
import org.yvl.teamforge.security.user.CustomUserDetailsService;
import org.yvl.teamforge.security.user.UserPrincipal;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
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

            Claims claims = jwtService.getClaims(token);

            Long userId = claims.get("userId", Long.class);

            if (userId == null) {
                throw new JwtException("JWT does not contain userId");
            }

            String userEmail = claims.getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (!(userDetails instanceof UserPrincipal userPrincipal)) {
                throw new JwtException("Invalid authenticated principal");
            }

            if (!userPrincipal.getUser().getId().equals(userId)) {
                throw new JwtException("JWT userId does not match authenticated user");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("JWT expired", e)
            );
        } catch (JwtException | UsernameNotFoundException e) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("JWT error", e)
            );
        }

    }
}
