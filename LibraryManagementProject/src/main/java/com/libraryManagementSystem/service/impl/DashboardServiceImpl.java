package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.DashboardAnalyticsDto;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.BookIssueStatus;
import com.libraryManagementSystem.enums.FineStatus;
import com.libraryManagementSystem.mapper.AuditLogMapper;
import com.libraryManagementSystem.repository.*;
import com.libraryManagementSystem.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public DashboardAnalyticsDto getAnalytics() {
        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();

        long activeIssues = bookIssueRepository.findByStatus(BookIssueStatus.ISSUED).size()
                + bookIssueRepository.findByStatus(BookIssueStatus.RENEWED).size()
                + bookIssueRepository.findByStatus(BookIssueStatus.OVERDUE).size();

        long overdueIssues = bookIssueRepository.findByStatus(BookIssueStatus.OVERDUE).size();

        BigDecimal totalFines = fineRepository.findByStatus(FineStatus.PAID).stream()
                .map(Fine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fetch recent activities (limit to 10)
        List<AuditLog> rawLogs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        List<AuditLog> sublist = rawLogs.subList(0, Math.min(rawLogs.size(), 10));
        var recentActivities = sublist.stream()
                .map(auditLogMapper::toDto)
                .collect(Collectors.toList());

        // Category Distribution
        List<Map<String, Object>> categoryDistribution = categoryRepository.findAll().stream()
                .map(cat -> {
                    long bookCount = bookRepository.findAll().stream()
                            .filter(b -> b.getCategory().getId().equals(cat.getId()))
                            .count();
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", cat.getName());
                    map.put("count", bookCount);
                    return map;
                })
                .collect(Collectors.toList());

        // Borrow trend for past 6 months (mock database stats dynamically grouped by month)
        List<Map<String, Object>> borrowTrend = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            String monthName = targetDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            
            // Filter issues created in this month
            long issueCount = bookIssueRepository.findAll().stream()
                    .filter(issue -> {
                        LocalDate issueLocalDate = issue.getIssueDate();
                        return issueLocalDate.getYear() == targetDate.getYear() && 
                               issueLocalDate.getMonth() == targetDate.getMonth();
                    })
                    .count();

            Map<String, Object> map = new HashMap<>();
            map.put("month", monthName);
            map.put("borrows", issueCount);
            borrowTrend.add(map);
        }

        return DashboardAnalyticsDto.builder()
                .totalBooks(totalBooks)
                .totalUsers(totalUsers)
                .totalBorrowedBooks(activeIssues)
                .totalOverdueBooks(overdueIssues)
                .totalFinesCollected(totalFines)
                .borrowTrend(borrowTrend)
                .categoryDistribution(categoryDistribution)
                .recentActivities(recentActivities)
                .build();
    }
}
