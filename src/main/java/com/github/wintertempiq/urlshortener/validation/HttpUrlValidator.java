package com.github.wintertempiq.urlshortener.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;

public class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            URI uri = URI.create(value);
            return ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                    && uri.getHost() != null && !uri.getHost().isBlank();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
