package com.libraryManagementSystem.scheduler;

import com.libraryManagementSystem.entity.BookIssue;
import com.libraryManagementSystem.enums.BookIssueStatus;
import com.libraryManagementSystem.repository.BookIssueRepository;
import com.libraryManagementSystem.repository.NotificationRepository;
import com.libraryManagementSystem.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class LibraryScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LibraryScheduler.class);

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReservationService reservationService;

    // Run at midnight every day
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processOverdueBooksAndExpiredHolds() {
        logger.info("Starting Library Scheduler tasks...");

        // 1. Process Overdue Book Issues
        List<BookIssue> activeIssues = bookIssueRepository.findByStatus(BookIssueStatus.ISSUED);
        activeIssues.addAll(bookIssueRepository.findByStatus(BookIssueStatus.RENEWED));
        
        LocalDate today = LocalDate.now();
        int overdueCount = 0;

        for (BookIssue issue : activeIssues) {
            if (issue.getDueDate().isBefore(today)) {
                issue.setStatus(BookIssueStatus.OVERDUE);
                bookIssueRepository.save(issue);
                overdueCount++;

                // Notify User
                com.libraryManagementSystem.entity.Notification notification = com.libraryManagementSystem.entity.Notification.builder()
                        .user(issue.getUser())
                        .message("ALERT: Your borrowed book '" + issue.getBookCopy().getBook().getTitle() + "' is overdue. Please return it immediately.")
                        .type("OVERDUE")
                        .build();
                notificationRepository.save(notification);
            }
        }

        logger.info("Marked {} book issues as OVERDUE.", overdueCount);

        // 2. Process Expired Reservations/Holds
        reservationService.checkExpiredReservations();
        logger.info("Checked and processed expired book reservations.");
    }
}
