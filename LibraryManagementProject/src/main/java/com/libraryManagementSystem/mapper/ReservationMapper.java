package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.ReservationDto;
import com.libraryManagementSystem.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(reservation.getUser().getFirstName() + \" \" + reservation.getUser().getLastName())")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    ReservationDto toDto(Reservation reservation);
}
