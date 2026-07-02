package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.UserDto;
import com.libraryManagementSystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", source = "role.name")
    UserDto toDto(User user);
}
