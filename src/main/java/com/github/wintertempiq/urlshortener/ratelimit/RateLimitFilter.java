package com.github.wintertempiq.urlshortener.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final KeyExtractor keyExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.equals("/api/v1/auth/login") && !path.equals("/api/v1/users/register") &&
                !path.equals("/api/v1/links") && !path.startsWith("/r/")) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);

        List<RateLimitKey> keys = keyExtractor.extractKeys(wrapped);

        if (keys.isEmpty()) {
            filterChain.doFilter(wrapped, response);
            return;
        }

        boolean allowed = rateLimiter.isAllowed(keys.toArray(new RateLimitKey[0]));

        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests...\"}");
            return;
        }

        filterChain.doFilter(wrapped, response);
    }
}
