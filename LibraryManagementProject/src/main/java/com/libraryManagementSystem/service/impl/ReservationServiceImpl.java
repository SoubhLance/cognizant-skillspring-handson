package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.ReservationDto;
import com.libraryManagementSystem.dto.ReservationRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.ReservationStatus;
import com.libraryManagementSystem.mapper.ReservationMapper;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.repository.NotificationRepository;
import com.libraryManagementSystem.repository.ReservationRepository;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationDto createReservation(ReservationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Set expiration date to 3 days from now
        LocalDateTime reservationTime = LocalDateTime.now();
        LocalDateTime expirationTime = reservationTime.plusDays(3);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(reservationTime)
                .status(ReservationStatus.PENDING)
                .expirationDate(expirationTime)
                .build();

        reservation = reservationRepository.save(reservation);

        // Notify user
        Notification notification = Notification.builder()
                .user(user)
                .message("You have reserved '" + book.getTitle() + "'. Hold expires on " + expirationTime)
                .type("RESERVATION")
                .build();
        notificationRepository.save(notification);

        return reservationMapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationDto cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        // Notify user
        Notification notification = Notification.builder()
                .user(reservation.getUser())
                .message("Your reservation for '" + reservation.getBook().getTitle() + "' has been cancelled.")
                .type("RESERVATION")
                .build();
        notificationRepository.save(notification);

        return reservationMapper.toDto(reservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkExpiredReservations() {
        List<Reservation> pending = reservationRepository.findByStatus(ReservationStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();

        for (Reservation res : pending) {
            if (res.getExpirationDate().isBefore(now)) {
                res.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(res);

                Notification notification = Notification.builder()
                        .user(res.getUser())
                        .message("Your reservation for '" + res.getBook().getTitle() + "' has expired.")
                        .type("RESERVATION")
                        .build();
                notificationRepository.save(notification);
            }
        }
    }
}
