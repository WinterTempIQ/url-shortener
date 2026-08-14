package com.github.wintertempiq.urlshortener.user.mapper;

import com.github.wintertempiq.urlshortener.user.dto.NewUserDto;
import com.github.wintertempiq.urlshortener.user.dto.UserDto;
import com.github.wintertempiq.urlshortener.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto userToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User newUserDtoToUser(NewUserDto dto);

    User userDtoToUser(UserDto dto);
}
