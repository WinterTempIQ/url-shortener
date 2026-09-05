package com.github.wintertempiq.urlshortener.link.controller;

import com.github.wintertempiq.urlshortener.exceptions.ApiError;
import com.github.wintertempiq.urlshortener.link.service.RedirectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
@Tag(name = "Redirect", description = "Resolve short codes to original URLs (HTTP redirect)")
public class RedirectController {

    private final RedirectService redirectService;

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to the original URL",
            description = "Resolves the short code and issues an HTTP 302 redirect to the original URL")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect to the original URL"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Short code not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "410", description = "Link has expired",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> redirect(
            @Parameter(description = "Short code to resolve", example = "aB3xK9z")
            @PathVariable String shortCode) {
        String originalUrl = redirectService.resolveUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}