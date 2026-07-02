package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.PublisherDto;
import com.libraryManagementSystem.dto.PublisherRequest;
import com.libraryManagementSystem.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PublisherDto>>> getAllPublishers() {
        List<PublisherDto> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(ApiResponse.success(publishers, "Publishers list fetched"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublisherDto>> getPublisherById(@PathVariable Long id) {
        PublisherDto publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(ApiResponse.success(publisher, "Publisher details fetched"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<PublisherDto>> createPublisher(@Valid @RequestBody PublisherRequest request) {
        PublisherDto publisher = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(publisher, "Publisher created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<PublisherDto>> updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherRequest request) {
        PublisherDto publisher = publisherService.updatePublisher(id, request);
        return ResponseEntity.ok(ApiResponse.success(publisher, "Publisher updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok(ApiResponse.success("Publisher deleted successfully"));
    }
}
