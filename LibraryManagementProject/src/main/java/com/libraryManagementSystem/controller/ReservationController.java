package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.ReservationDto;
import com.libraryManagementSystem.dto.ReservationRequest;
import com.libraryManagementSystem.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDto reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Book reservation placed successfully"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto>> cancelReservation(@PathVariable Long id) {
        ReservationDto reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation cancelled successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        List<ReservationDto> list = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(list, "All system reservations fetched"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getUserReservations(@PathVariable Long userId) {
        List<ReservationDto> list = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(ApiResponse.success(list, "User reservations fetched"));
    }
}
