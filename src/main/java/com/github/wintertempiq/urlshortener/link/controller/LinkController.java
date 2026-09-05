package com.github.wintertempiq.urlshortener.link.controller;

import com.github.wintertempiq.urlshortener.exceptions.ApiError;
import com.github.wintertempiq.urlshortener.link.dto.CreateLinkRequest;
import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
@Tag(name = "Links", description = "Manage shortened links")
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a shortened link",
            description = "Shortens the provided original URL for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Link created",
                    content = @Content(schema = @Schema(implementation = LinkShortDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL provided",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public LinkShortDto createLink(@Valid @RequestBody CreateLinkRequest request) {
        return linkService.createLink(request);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List user links",
            description = "Returns a paginated list of links created by the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Links returned",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Page<LinkShortDto> getLinksByUser(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                             Pageable pageable) {
        return linkService.findLinksByUser(pageable);
    }

    @GetMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get link details",
            description = "Returns full details for the given short code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Link details returned",
                    content = @Content(schema = @Schema(implementation = LinkFullDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Link not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "410", description = "Link has expired",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public LinkFullDto getLink(@Parameter(description = "Short code of the link", example = "aB3xK9z")
                               @PathVariable String shortCode) {
        return linkService.fullLinksInfoByShortCode(shortCode);
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a link",
            description = "Deletes the link with the given short code")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Link deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Link not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void deleteLinkByShortCode(@Parameter(description = "Short code of the link to delete", example = "aB3xK9z")
                                      @PathVariable String shortCode) {
        linkService.deleteLinkByShortCode(shortCode);
    }
}