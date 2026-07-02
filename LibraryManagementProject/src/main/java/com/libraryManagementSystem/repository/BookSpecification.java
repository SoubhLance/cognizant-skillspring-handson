package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.Author;
import com.libraryManagementSystem.entity.Book;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(title)) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Book> hasIsbn(String isbn) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(isbn)) return null;
            return cb.equal(root.get("isbn"), isbn);
        };
    }

    public static Specification<Book> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Book> hasPublisherId(Long publisherId) {
        return (root, query, cb) -> {
            if (publisherId == null) return null;
            return cb.equal(root.get("publisher").get("id"), publisherId);
        };
    }

    public static Specification<Book> hasAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) return null;
            Join<Book, Author> authorJoin = root.join("authors");
            return cb.equal(authorJoin.get("id"), authorId);
        };
    }

    public static Specification<Book> isAvailable() {
        return (root, query, cb) -> cb.greaterThan(root.get("availableCopies"), 0);
    }
}
