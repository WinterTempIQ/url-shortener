package com.github.wintertempiq.urlshortener.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private Long id;

    private String email;
}
