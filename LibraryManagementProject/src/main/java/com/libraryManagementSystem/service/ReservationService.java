package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.ReservationDto;
import com.libraryManagementSystem.dto.ReservationRequest;

import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(ReservationRequest request);
    ReservationDto cancelReservation(Long reservationId);
    List<ReservationDto> getAllReservations();
    List<ReservationDto> getUserReservations(Long userId);
    void checkExpiredReservations();
}
