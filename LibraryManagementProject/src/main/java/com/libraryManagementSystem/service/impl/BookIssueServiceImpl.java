package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BookIssueDto;
import com.libraryManagementSystem.dto.BookIssueRequest;
import com.libraryManagementSystem.dto.BookReturnRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.BookCopyStatus;
import com.libraryManagementSystem.enums.BookIssueStatus;
import com.libraryManagementSystem.enums.FineStatus;
import com.libraryManagementSystem.enums.UserStatus;
import com.libraryManagementSystem.exception.FineException;
import com.libraryManagementSystem.mapper.BookIssueMapper;
import com.libraryManagementSystem.repository.*;
import com.libraryManagementSystem.service.BookIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookIssueServiceImpl implements BookIssueService {

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BookIssueMapper bookIssueMapper;

    @Value("${app.library.fine-per-day:1.50}")
    private double finePerDay;

    @Override
    @Transactional
    public BookIssueDto issueBook(BookIssueRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User account is not active");
        }

        // Check unpaid fines
        long unpaidFinesCount = fineRepository.countByBookIssueUserIdAndStatus(user.getId(), FineStatus.UNPAID);
        if (unpaidFinesCount > 0) {
            throw new FineException("User has outstanding unpaid fines and cannot borrow books.");
        }

        // Check borrow limits
        long currentBorrowedCount = bookIssueRepository.countByUserIdAndStatus(user.getId(), BookIssueStatus.ISSUED)
                + bookIssueRepository.countByUserIdAndStatus(user.getId(), BookIssueStatus.RENEWED)
                + bookIssueRepository.countByUserIdAndStatus(user.getId(), BookIssueStatus.OVERDUE);

        int maxBooks = 5;
        int maxDays = 14;

        String roleName = user.getRole().getName();
        if ("ROLE_FACULTY".equals(roleName)) {
            maxBooks = 10;
            maxDays = 30;
        } else if ("ROLE_LIBRARIAN".equals(roleName)) {
            maxBooks = 5;
            maxDays = 21;
        } else if ("ROLE_ADMIN".equals(roleName)) {
            maxBooks = 10;
            maxDays = 30;
        }

        if (currentBorrowedCount >= maxBooks) {
            throw new RuntimeException("User borrowing limit of " + maxBooks + " books has been reached.");
        }

        // Check Copy availability
        BookCopy copy = bookCopyRepository.findByBarcode(request.getBarcode())
                .orElseThrow(() -> new RuntimeException("Book copy with barcode " + request.getBarcode() + " not found."));

        if (copy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new RuntimeException("This book copy is currently " + copy.getStatus());
        }

        // Create transaction
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : issueDate.plusDays(maxDays);

        BookIssue issue = BookIssue.builder()
                .user(user)
                .bookCopy(copy)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .status(BookIssueStatus.ISSUED)
                .build();

        // Update counts and status
        copy.setStatus(BookCopyStatus.ISSUED);
        bookCopyRepository.save(copy);

        Book book = copy.getBook();
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() - 1));
        bookRepository.save(book);

        issue = bookIssueRepository.save(issue);

        // Notify
        Notification notification = Notification.builder()
                .user(user)
                .message("You have borrowed '" + book.getTitle() + "'. Due date: " + dueDate)
                .type("BORROW")
                .build();
        notificationRepository.save(notification);

        return bookIssueMapper.toDto(issue);
    }

    @Override
    @Transactional
    public BookIssueDto returnBook(BookReturnRequest request) {
        BookCopy copy = bookCopyRepository.findByBarcode(request.getBarcode())
                .orElseThrow(() -> new RuntimeException("Book copy not found"));

        BookIssue issue = bookIssueRepository.findFirstByBookCopyBarcodeAndStatusInOrderByIdDesc(
                request.getBarcode(), Arrays.asList(BookIssueStatus.ISSUED, BookIssueStatus.RENEWED, BookIssueStatus.OVERDUE))
                .orElseThrow(() -> new RuntimeException("No active borrow transaction found for copy: " + request.getBarcode()));

        LocalDate returnDate = LocalDate.now();
        issue.setReturnDate(returnDate);
        issue.setStatus(BookIssueStatus.RETURNED);

        // Update Copy
        copy.setStatus(BookCopyStatus.AVAILABLE);
        copy.setBookCondition(request.getCondition());
        bookCopyRepository.save(copy);

        // Update Book count
        Book book = copy.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Assess late fine
        if (returnDate.isAfter(issue.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(issue.getDueDate(), returnDate);
            BigDecimal fineAmount = BigDecimal.valueOf(daysLate * finePerDay);

            Fine fine = Fine.builder()
                    .bookIssue(issue)
                    .amount(fineAmount)
                    .status(FineStatus.UNPAID)
                    .build();
            fineRepository.save(fine);

            Notification fineNotif = Notification.builder()
                    .user(issue.getUser())
                    .message("Late return fine generated: $" + fineAmount + " for " + daysLate + " days overdue of book '" + book.getTitle() + "'")
                    .type("FINE")
                    .build();
            notificationRepository.save(fineNotif);
        }

        issue = bookIssueRepository.save(issue);

        Notification notification = Notification.builder()
                .user(issue.getUser())
                .message("Returned '" + book.getTitle() + "' successfully in " + request.getCondition() + " condition.")
                .type("RETURN")
                .build();
        notificationRepository.save(notification);

        return bookIssueMapper.toDto(issue);
    }

    @Override
    @Transactional
    public BookIssueDto renewBook(Long issueId) {
        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Borrow transaction not found"));

        if (issue.getStatus() == BookIssueStatus.RETURNED) {
            throw new RuntimeException("Returned books cannot be renewed");
        }

        // Extend due date by 14 days
        LocalDate newDueDate = issue.getDueDate().plusDays(14);
        issue.setDueDate(newDueDate);
        issue.setStatus(BookIssueStatus.RENEWED);

        issue = bookIssueRepository.save(issue);

        Notification notification = Notification.builder()
                .user(issue.getUser())
                .message("Book '" + issue.getBookCopy().getBook().getTitle() + "' renewed. New due date: " + newDueDate)
                .type("RENEW")
                .build();
        notificationRepository.save(notification);

        return bookIssueMapper.toDto(issue);
    }

    @Override
    public List<BookIssueDto> getAllIssues() {
        return bookIssueRepository.findAll().stream()
                .map(bookIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookIssueDto> getUserIssues(Long userId) {
        return bookIssueRepository.findByUserId(userId).stream()
                .map(bookIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookIssueDto> getOverdueIssues() {
        return bookIssueRepository.findByStatus(BookIssueStatus.OVERDUE).stream()
                .map(bookIssueMapper::toDto)
                .collect(Collectors.toList());
    }
}
