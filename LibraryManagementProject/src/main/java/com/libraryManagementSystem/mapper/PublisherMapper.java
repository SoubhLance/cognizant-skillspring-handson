package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.PublisherDto;
import com.libraryManagementSystem.dto.PublisherRequest;
import com.libraryManagementSystem.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PublisherMapper {
    PublisherDto toDto(Publisher publisher);
    Publisher toEntity(PublisherRequest request);
    void updateEntity(PublisherRequest request, @MappingTarget Publisher publisher);
}
