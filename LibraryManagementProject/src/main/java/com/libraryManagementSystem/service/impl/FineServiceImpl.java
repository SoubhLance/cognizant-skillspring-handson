package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.FineDto;
import com.libraryManagementSystem.dto.PayFineRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.FineStatus;
import com.libraryManagementSystem.mapper.FineMapper;
import com.libraryManagementSystem.repository.FineRepository;
import com.libraryManagementSystem.repository.NotificationRepository;
import com.libraryManagementSystem.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FineServiceImpl implements FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FineMapper fineMapper;

    @Override
    @Transactional
    public FineDto payFine(PayFineRequest request) {
        Fine fine = fineRepository.findById(request.getFineId())
                .orElseThrow(() -> new RuntimeException("Fine record not found"));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new RuntimeException("Fine has already been paid");
        }

        fine.setStatus(FineStatus.PAID);
        fine.setPaymentDate(LocalDateTime.now());
        fine.setTransactionId(request.getTransactionId());
        fine = fineRepository.save(fine);

        // Notify
        Notification notification = Notification.builder()
                .user(fine.getBookIssue().getUser())
                .message("Fine payment of $" + fine.getAmount() + " processed successfully. Txn ID: " + request.getTransactionId())
                .type("FINE")
                .build();
        notificationRepository.save(notification);

        return fineMapper.toDto(fine);
    }

    @Override
    @Transactional
    public FineDto waiveFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine record not found"));

        fine.setStatus(FineStatus.WAIVED);
        fine.setPaymentDate(LocalDateTime.now());
        fine = fineRepository.save(fine);

        // Notify
        Notification notification = Notification.builder()
                .user(fine.getBookIssue().getUser())
                .message("Fine amount of $" + fine.getAmount() + " has been waived by librarian.")
                .type("FINE")
                .build();
        notificationRepository.save(notification);

        return fineMapper.toDto(fine);
    }

    @Override
    public List<FineDto> getAllFines() {
        return fineRepository.findAll().stream()
                .map(fineMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FineDto> getUserFines(Long userId) {
        return fineRepository.findByBookIssueUserId(userId).stream()
                .map(fineMapper::toDto)
                .collect(Collectors.toList());
    }
}
