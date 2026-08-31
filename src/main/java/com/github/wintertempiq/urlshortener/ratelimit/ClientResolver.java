package com.github.wintertempiq.urlshortener.ratelimit;

import com.github.wintertempiq.urlshortener.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientResolver {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final AppProperties appProperties;

    public String resolveIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        boolean isTrustedProxy = appProperties.isBehindProxy()
                && appProperties.getTrustedProxies().contains(remoteAddr);

        if (isTrustedProxy) {
            String forwarded = request.getHeader(X_FORWARDED_FOR);
            if (forwarded != null && !forwarded.isBlank()) {
                String first = forwarded.split(",")[0].trim();
                if (!first.isEmpty()) {
                    return first;
                }
            }
        }

        return remoteAddr;
    }
}
