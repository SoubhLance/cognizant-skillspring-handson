package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.BookCopyStatus;
import com.libraryManagementSystem.repository.*;
import com.libraryManagementSystem.service.ReportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Override
    public byte[] exportBooksToCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Title,ISBN,Category,Publisher,Total Copies,Available Copies\n");
        for (Book book : bookRepository.findAll()) {
            sb.append(book.getId()).append(",")
              .append("\"").append(book.getTitle().replace("\"", "\"\"")).append("\",")
              .append(book.getIsbn()).append(",")
              .append("\"").append(book.getCategory().getName().replace("\"", "\"\"")).append("\",")
              .append("\"").append(book.getPublisher().getName().replace("\"", "\"\"")).append("\",")
              .append(book.getTotalCopies()).append(",")
              .append(book.getAvailableCopies()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportIssuesToCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Borrower Email,Borrower Name,Barcode,Book Title,Issue Date,Due Date,Return Date,Status\n");
        for (BookIssue issue : bookIssueRepository.findAll()) {
            String returnDate = issue.getReturnDate() != null ? issue.getReturnDate().toString() : "";
            sb.append(issue.getId()).append(",")
              .append(issue.getUser().getEmail()).append(",")
              .append("\"").append(issue.getUser().getFirstName()).append(" ").append(issue.getUser().getLastName()).append("\",")
              .append(issue.getBookCopy().getBarcode()).append(",")
              .append("\"").append(issue.getBookCopy().getBook().getTitle().replace("\"", "\"\"")).append("\",")
              .append(issue.getIssueDate()).append(",")
              .append(issue.getDueDate()).append(",")
              .append(returnDate).append(",")
              .append(issue.getStatus()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportBooksToExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Books");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Title", "ISBN", "Category", "Publisher", "Total Copies", "Available Copies"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Book book : bookRepository.findAll()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getIsbn());
                row.createCell(3).setCellValue(book.getCategory().getName());
                row.createCell(4).setCellValue(book.getPublisher().getName());
                row.createCell(5).setCellValue(book.getTotalCopies());
                row.createCell(6).setCellValue(book.getAvailableCopies());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    @Override
    public byte[] exportIssuesToExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Borrow Transactions");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Borrower Email", "Borrower Name", "Barcode", "Book Title", "Issue Date", "Due Date", "Return Date", "Status"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (BookIssue issue : bookIssueRepository.findAll()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(issue.getId());
                row.createCell(1).setCellValue(issue.getUser().getEmail());
                row.createCell(2).setCellValue(issue.getUser().getFirstName() + " " + issue.getUser().getLastName());
                row.createCell(3).setCellValue(issue.getBookCopy().getBarcode());
                row.createCell(4).setCellValue(issue.getBookCopy().getBook().getTitle());
                row.createCell(5).setCellValue(issue.getIssueDate().toString());
                row.createCell(6).setCellValue(issue.getDueDate().toString());
                row.createCell(7).setCellValue(issue.getReturnDate() != null ? issue.getReturnDate().toString() : "");
                row.createCell(8).setCellValue(issue.getStatus().toString());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    @Override
    public byte[] exportBooksToPdf() {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Library Books Catalog Report");
                contentStream.endText();

                int yPosition = 710;
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(50, yPosition);

                for (Book book : bookRepository.findAll()) {
                    if (yPosition < 50) {
                        contentStream.endText();
                        // Real enterprise reports would handle multi-page dynamically. For demo we break or cap.
                        break;
                    }
                    String text = String.format("ID: %d | %s | ISBN: %s | Total: %d | Avail: %d",
                            book.getId(), book.getTitle(), book.getIsbn(), book.getTotalCopies(), book.getAvailableCopies());
                    // Remove special characters that PDFBox cannot render
                    text = text.replaceAll("[^\\x20-\\x7e]", "");
                    contentStream.showText(text);
                    contentStream.newLineAtOffset(0, -15);
                    yPosition -= 15;
                }
                contentStream.endText();
            }
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Override
    public byte[] exportIssuesToPdf() {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Borrow Transactions Report");
                contentStream.endText();

                int yPosition = 710;
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.newLineAtOffset(50, yPosition);

                for (BookIssue issue : bookIssueRepository.findAll()) {
                    if (yPosition < 50) {
                        contentStream.endText();
                        break;
                    }
                    String text = String.format("ID: %d | %s | Barcode: %s | Status: %s | Due: %s",
                            issue.getId(), issue.getUser().getEmail(), issue.getBookCopy().getBarcode(),
                            issue.getStatus(), issue.getDueDate());
                    text = text.replaceAll("[^\\x20-\\x7e]", "");
                    contentStream.showText(text);
                    contentStream.newLineAtOffset(0, -15);
                    yPosition -= 15;
                }
                contentStream.endText();
            }
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    @Override
    @Transactional
    public void importBooksFromCsv(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            // Skip header
            reader.readLine();
            
            Category defaultCategory = categoryRepository.findAll().stream().findFirst()
                    .orElseGet(() -> categoryRepository.save(Category.builder().name("General").description("Default Category").build()));
            Publisher defaultPublisher = publisherRepository.findAll().stream().findFirst()
                    .orElseGet(() -> publisherRepository.save(Publisher.builder().name("Default Publisher").contactEmail("default@pub.com").build()));
            Author defaultAuthor = authorRepository.findAll().stream().findFirst()
                    .orElseGet(() -> authorRepository.save(Author.builder().name("Unknown Author").build()));

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (fields.length < 3) continue;

                String title = fields[0].replace("\"", "").trim();
                String isbn = fields[1].trim();
                int totalCopies = fields.length >= 3 ? Integer.parseInt(fields[2].trim()) : 1;

                if (bookRepository.existsByIsbn(isbn)) continue;

                Book book = Book.builder()
                        .title(title)
                        .isbn(isbn)
                        .category(defaultCategory)
                        .publisher(defaultPublisher)
                        .authors(new HashSet<>(Collections.singletonList(defaultAuthor)))
                        .totalCopies(totalCopies)
                        .availableCopies(totalCopies)
                        .build();

                book = bookRepository.save(book);

                // Create copies
                for (int i = 1; i <= totalCopies; i++) {
                    BookCopy copy = BookCopy.builder()
                            .book(book)
                            .barcode(isbn + "-COPY-" + i)
                            .status(BookCopyStatus.AVAILABLE)
                            .bookCondition("Good")
                            .build();
                    bookCopyRepository.save(copy);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse and import CSV", e);
        }
    }
}
