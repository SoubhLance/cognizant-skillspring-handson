package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/books/csv")
    public ResponseEntity<byte[]> exportBooksCsv() {
        byte[] csvBytes = reportService.exportBooksToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvBytes);
    }

    @GetMapping("/issues/csv")
    public ResponseEntity<byte[]> exportIssuesCsv() {
        byte[] csvBytes = reportService.exportIssuesToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=issues.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvBytes);
    }

    @GetMapping("/books/excel")
    public ResponseEntity<byte[]> exportBooksExcel() {
        byte[] excelBytes = reportService.exportBooksToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/issues/excel")
    public ResponseEntity<byte[]> exportIssuesExcel() {
        byte[] excelBytes = reportService.exportIssuesToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=issues.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/books/pdf")
    public ResponseEntity<byte[]> exportBooksPdf() {
        byte[] pdfBytes = reportService.exportBooksToPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/issues/pdf")
    public ResponseEntity<byte[]> exportIssuesPdf() {
        byte[] pdfBytes = reportService.exportIssuesToPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=issues.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/books/import")
    public ResponseEntity<ApiResponse<Void>> importBooks(@RequestParam("file") MultipartFile file) throws IOException {
        reportService.importBooksFromCsv(file.getInputStream());
        return ResponseEntity.ok(ApiResponse.success("Books catalog imported successfully from CSV"));
    }
}
