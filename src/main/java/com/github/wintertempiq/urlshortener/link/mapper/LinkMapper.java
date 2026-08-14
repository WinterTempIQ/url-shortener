package com.github.wintertempiq.urlshortener.link.mapper;

import com.github.wintertempiq.urlshortener.link.dto.LinkFullDto;
import com.github.wintertempiq.urlshortener.link.dto.LinkShortDto;
import com.github.wintertempiq.urlshortener.link.entity.Link;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LinkMapper {

    LinkShortDto linkToLinkShortDto(Link link);

    LinkFullDto linkToLinkFullDto(Link link);
}
