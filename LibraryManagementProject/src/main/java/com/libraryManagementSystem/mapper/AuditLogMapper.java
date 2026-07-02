package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.AuditLogDto;
import com.libraryManagementSystem.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(log.getUser() != null ? log.getUser().getFirstName() + \" \" + log.getUser().getLastName() : \"SYSTEM\")")
    AuditLogDto toDto(AuditLog log);
}
