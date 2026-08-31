package com.github.wintertempiq.urlshortener.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeyExtractor {

    private final ObjectMapper objectMapper;
    private final ClientResolver clientResolver;

    public List<RateLimitKey> extractKeys(HttpServletRequest request) {
        List<RateLimitKey> keys = new ArrayList<>();
        String path = request.getRequestURI();
        String ip = clientResolver.resolveIp(request);
        String method = request.getMethod();

        if (path.equals("/api/v1/users/register")) {
            keys.add(new RateLimitKey("register:ip:" + ip, RuleType.IP));
        }

        if (path.equals("/api/v1/auth/login")) {
            keys.add(new RateLimitKey("login:ip:" + ip, RuleType.IP));
            String email = extractEmail(request);
            if (email != null && !email.isEmpty()) {
                keys.add(new RateLimitKey("login:email:" + email, RuleType.EMAIL));
            }
        }

        if (path.equals("/api/v1/links") && "POST".equalsIgnoreCase(method)) {
            keys.add(new RateLimitKey("links:ip:" + ip, RuleType.CREATE_LINK));
        }

        if (path.startsWith("/r/") && "GET".equalsIgnoreCase(method)) {
            String shortCode = path.substring(3);

            keys.add(new RateLimitKey("redirect:ip:" + ip, RuleType.REDIRECT_SHORTCODE));

            keys.add(new RateLimitKey("redirect:code:" + shortCode + ":ip:" + ip, RuleType.REDIRECT));

        }

        return keys;

    }

    private String extractEmail(HttpServletRequest request) {
        if (!(request instanceof CachedBodyHttpServletRequest)) {
            return null;
        }

        String body = ((CachedBodyHttpServletRequest) request).getCachedBody();

        if (body == null || body.isEmpty()) {
            return null;
        }

        try {
            Map<String, Object> json = objectMapper.readValue(body, Map.class);
            return (String)  json.get("email");
        } catch (IOException e) {
            return null;
        }

    }
}
