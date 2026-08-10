package com.github.wintertempiq.urlshortener.security;

import com.github.wintertempiq.urlshortener.exceptions.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("Not authenticated.");
        }
        return auth.getName();
    }
}
