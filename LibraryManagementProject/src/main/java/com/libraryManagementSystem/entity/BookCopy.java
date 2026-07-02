package com.libraryManagementSystem.entity;

import com.libraryManagementSystem.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('AVAILABLE', 'ISSUED', 'RESERVED', 'DAMAGED', 'LOST')", nullable = false)
    private BookCopyStatus status;

    @Column(name = "book_condition", length = 100)
    @Builder.Default
    private String bookCondition = "Good";
}
