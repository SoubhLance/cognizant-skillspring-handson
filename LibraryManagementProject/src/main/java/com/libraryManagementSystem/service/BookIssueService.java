package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookIssueDto;
import com.libraryManagementSystem.dto.BookIssueRequest;
import com.libraryManagementSystem.dto.BookReturnRequest;

import java.util.List;

public interface BookIssueService {
    BookIssueDto issueBook(BookIssueRequest request);
    BookIssueDto returnBook(BookReturnRequest request);
    BookIssueDto renewBook(Long issueId);
    List<BookIssueDto> getAllIssues();
    List<BookIssueDto> getUserIssues(Long userId);
    List<BookIssueDto> getOverdueIssues();
}
