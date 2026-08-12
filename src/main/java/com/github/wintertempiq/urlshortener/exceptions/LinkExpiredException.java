package com.github.wintertempiq.urlshortener.exceptions;

public class LinkExpiredException extends RuntimeException {
    public LinkExpiredException(String message) {
        super(message);
    }
}
