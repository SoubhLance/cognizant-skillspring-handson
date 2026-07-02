package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.FineDto;
import com.libraryManagementSystem.dto.PayFineRequest;
import com.libraryManagementSystem.service.FineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private FineService fineService;

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<FineDto>> payFine(@Valid @RequestBody PayFineRequest request) {
        FineDto fine = fineService.payFine(request);
        return ResponseEntity.ok(ApiResponse.success(fine, "Fine paid successfully"));
    }

    @PostMapping("/{id}/waive")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<FineDto>> waiveFine(@PathVariable Long id) {
        FineDto fine = fineService.waiveFine(id);
        return ResponseEntity.ok(ApiResponse.success(fine, "Fine waived successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<FineDto>>> getAllFines() {
        List<FineDto> list = fineService.getAllFines();
        return ResponseEntity.ok(ApiResponse.success(list, "All system fines fetched"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FineDto>>> getUserFines(@PathVariable Long userId) {
        List<FineDto> list = fineService.getUserFines(userId);
        return ResponseEntity.ok(ApiResponse.success(list, "User fines fetched"));
    }
}
