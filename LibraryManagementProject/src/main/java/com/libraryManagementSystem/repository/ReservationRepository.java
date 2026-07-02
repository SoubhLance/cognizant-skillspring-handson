package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.Reservation;
import com.libraryManagementSystem.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByBookIdAndStatus(Long bookId, ReservationStatus status);
    List<Reservation> findByStatus(ReservationStatus status);
}
