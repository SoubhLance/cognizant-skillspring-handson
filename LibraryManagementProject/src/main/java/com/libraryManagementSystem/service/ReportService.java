package com.libraryManagementSystem.service;

import java.io.InputStream;

public interface ReportService {
    byte[] exportBooksToCsv();
    byte[] exportIssuesToCsv();
    byte[] exportBooksToExcel();
    byte[] exportIssuesToExcel();
    byte[] exportBooksToPdf();
    byte[] exportIssuesToPdf();
    void importBooksFromCsv(InputStream inputStream);
}
