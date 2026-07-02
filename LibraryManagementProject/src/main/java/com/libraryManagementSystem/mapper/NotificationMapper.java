package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.NotificationDto;
import com.libraryManagementSystem.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "userId", source = "user.id")
    NotificationDto toDto(Notification notification);
}
