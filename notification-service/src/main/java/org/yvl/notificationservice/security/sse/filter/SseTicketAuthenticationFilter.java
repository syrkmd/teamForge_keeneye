package org.yvl.notificationservice.security.sse.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yvl.notificationservice.security.handler.JwtAuthenticationEntryPoint;
import org.yvl.notificationservice.sse.ticket.service.SseTicketService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SseTicketAuthenticationFilter extends OncePerRequestFilter {

    private final SseTicketService sseTicketService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private static final RequestMatcher REQUEST_MATCHER = PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/sse/stream");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !REQUEST_MATCHER.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ticket = request.getParameter("ticket");

        if (ticket == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Long> userId = sseTicketService.validateAndConsume(ticket);

        if (userId.isEmpty()) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid ticket")
            );
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId.get(),
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }
}
