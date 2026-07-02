package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.BookIssueDto;
import com.libraryManagementSystem.dto.BookIssueRequest;
import com.libraryManagementSystem.dto.BookReturnRequest;
import com.libraryManagementSystem.service.BookIssueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    @Autowired
    private BookIssueService bookIssueService;

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookIssueDto>> issueBook(@Valid @RequestBody BookIssueRequest request) {
        BookIssueDto issue = bookIssueService.issueBook(request);
        return ResponseEntity.ok(ApiResponse.success(issue, "Book issued successfully"));
    }

    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookIssueDto>> returnBook(@Valid @RequestBody BookReturnRequest request) {
        BookIssueDto issue = bookIssueService.returnBook(request);
        return ResponseEntity.ok(ApiResponse.success(issue, "Book returned successfully"));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<ApiResponse<BookIssueDto>> renewBook(@PathVariable Long id) {
        BookIssueDto issue = bookIssueService.renewBook(id);
        return ResponseEntity.ok(ApiResponse.success(issue, "Book loan renewed successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BookIssueDto>>> getAllIssues() {
        List<BookIssueDto> issues = bookIssueService.getAllIssues();
        return ResponseEntity.ok(ApiResponse.success(issues, "All borrow records fetched"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookIssueDto>>> getUserIssues(@PathVariable Long userId) {
        List<BookIssueDto> issues = bookIssueService.getUserIssues(userId);
        return ResponseEntity.ok(ApiResponse.success(issues, "User borrow history fetched"));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BookIssueDto>>> getOverdueIssues() {
        List<BookIssueDto> issues = bookIssueService.getOverdueIssues();
        return ResponseEntity.ok(ApiResponse.success(issues, "Overdue borrow records fetched"));
    }
}
