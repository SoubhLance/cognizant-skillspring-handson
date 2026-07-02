package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.FineDto;
import com.libraryManagementSystem.dto.PayFineRequest;

import java.util.List;

public interface FineService {
    FineDto payFine(PayFineRequest request);
    FineDto waiveFine(Long fineId);
    List<FineDto> getAllFines();
    List<FineDto> getUserFines(Long userId);
}
