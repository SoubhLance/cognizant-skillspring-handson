-- ==========================================
-- ENTERPRISE LIBRARY MANAGEMENT SYSTEM DATABASE
-- ==========================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Disable foreign key checks to allow clean drops if needed
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS email_verification_tokens;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS book_ratings;
DROP TABLE IF EXISTS book_reviews;
DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS book_issues;
DROP TABLE IF EXISTS book_copies;
DROP TABLE IF EXISTS book_authors;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS publishers;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------
-- Table roles
-- -----------------------------------------------------
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table permissions
-- -----------------------------------------------------
CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table role_permissions
-- -----------------------------------------------------
CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_email ON users(email);

-- -----------------------------------------------------
-- Table categories
-- -----------------------------------------------------
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table authors
-- -----------------------------------------------------
CREATE TABLE authors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    biography TEXT,
    birth_date DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table publishers
-- -----------------------------------------------------
CREATE TABLE publishers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    address TEXT,
    contact_email VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table books
-- -----------------------------------------------------
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    category_id INT NOT NULL,
    publisher_id INT NOT NULL,
    publication_year INT,
    description TEXT,
    cover_image_url VARCHAR(255),
    total_copies INT DEFAULT 0,
    available_copies INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers (id),
    CONSTRAINT chk_books_year CHECK (publication_year >= 1000 AND publication_year <= YEAR(CURDATE()) + 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_title ON books(title);

-- -----------------------------------------------------
-- Table book_authors
-- -----------------------------------------------------
CREATE TABLE book_authors (
    book_id INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table book_copies
-- -----------------------------------------------------
CREATE TABLE book_copies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    barcode VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('AVAILABLE', 'ISSUED', 'RESERVED', 'DAMAGED', 'LOST') DEFAULT 'AVAILABLE',
    book_condition VARCHAR(100) DEFAULT 'Good',
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_book_copies_barcode ON book_copies(barcode);

-- -----------------------------------------------------
-- Table book_issues
-- -----------------------------------------------------
CREATE TABLE book_issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    copy_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM('ISSUED', 'RETURNED', 'OVERDUE', 'RENEWED') DEFAULT 'ISSUED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_issues_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_book_issues_copy FOREIGN KEY (copy_id) REFERENCES book_copies (id),
    CONSTRAINT chk_issue_dates CHECK (due_date >= issue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_book_issues_dates ON book_issues(issue_date, due_date);

-- -----------------------------------------------------
-- Table reservations
-- -----------------------------------------------------
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'EXPIRED', 'CANCELLED') DEFAULT 'PENDING',
    expiration_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table fines
-- -----------------------------------------------------
CREATE TABLE fines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('UNPAID', 'PAID', 'WAIVED') DEFAULT 'UNPAID',
    payment_date TIMESTAMP NULL,
    transaction_id VARCHAR(100) NULL,
    CONSTRAINT fk_fines_issue FOREIGN KEY (issue_id) REFERENCES book_issues (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table wishlists
-- -----------------------------------------------------
CREATE TABLE wishlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_book (user_id, book_id),
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlists_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table book_reviews
-- -----------------------------------------------------
CREATE TABLE book_reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table book_ratings
-- -----------------------------------------------------
CREATE TABLE book_ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    rating_value INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_book_rating (user_id, book_id),
    CONSTRAINT fk_ratings_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_rating_range CHECK (rating_value >= 1 AND rating_value <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table refresh_tokens
-- -----------------------------------------------------
CREATE TABLE refresh_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table password_reset_tokens
-- -----------------------------------------------------
CREATE TABLE password_reset_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_pwd_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table email_verification_tokens
-- -----------------------------------------------------
CREATE TABLE email_verification_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_email_verif_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table notifications
-- -----------------------------------------------------
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table audit_logs
-- -----------------------------------------------------
CREATE TABLE audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- VIEWS
-- ==========================================

-- View for active overdue issues
CREATE OR REPLACE VIEW view_overdue_books AS
SELECT 
    bi.id AS issue_id,
    u.id AS user_id,
    CONCAT(u.first_name, ' ', u.last_name) AS borrower_name,
    u.email AS borrower_email,
    b.title AS book_title,
    bc.barcode AS copy_barcode,
    bi.issue_date,
    bi.due_date,
    DATEDIFF(CURDATE(), bi.due_date) AS days_overdue,
    (DATEDIFF(CURDATE(), bi.due_date) * 1.50) AS calculated_fine
FROM book_issues bi
JOIN users u ON bi.user_id = u.id
JOIN book_copies bc ON bi.copy_id = bc.id
JOIN books b ON bc.book_id = b.id
WHERE bi.status = 'ISSUED' AND bi.due_date < CURDATE();

-- View for book popularity statistics
CREATE OR REPLACE VIEW view_popular_books AS
SELECT 
    b.id AS book_id,
    b.title,
    b.isbn,
    COUNT(bi.id) AS borrow_count
FROM books b
JOIN book_copies bc ON b.id = bc.book_id
JOIN book_issues bi ON bc.id = bi.copy_id
GROUP BY b.id, b.title, b.isbn
ORDER BY borrow_count DESC;

-- ==========================================
-- PROCEDURES & FUNCTIONS
-- ==========================================

DELIMITER $$

-- Procedure to issue a book copy
CREATE PROCEDURE IssueBookProcess(
    IN p_user_id INT,
    IN p_copy_barcode VARCHAR(50),
    IN p_due_date DATE,
    OUT p_success BOOLEAN,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_copy_id INT DEFAULT NULL;
    DECLARE v_copy_status VARCHAR(50);
    DECLARE v_user_status VARCHAR(50);
    DECLARE v_unpaid_fines DECIMAL(10,2) DEFAULT 0.00;
    
    START TRANSACTION;
    
    -- Check user status
    SELECT status INTO v_user_status FROM users WHERE id = p_user_id;
    
    -- Check copy details
    SELECT id, status INTO v_copy_id, v_copy_status 
    FROM book_copies 
    WHERE barcode = p_copy_barcode;
    
    -- Check user unpaid fines
    SELECT COALESCE(SUM(amount), 0.00) INTO v_unpaid_fines
    FROM fines f
    JOIN book_issues bi ON f.issue_id = bi.id
    WHERE bi.user_id = p_user_id AND f.status = 'UNPAID';
    
    IF v_user_status IS NULL THEN
        SET p_success = FALSE;
        SET p_message = 'User not found.';
        ROLLBACK;
    ELSEIF v_user_status != 'ACTIVE' THEN
        SET p_success = FALSE;
        SET p_message = 'User account is inactive or suspended.';
        ROLLBACK;
    ELSEIF v_unpaid_fines > 0 THEN
        SET p_success = FALSE;
        SET p_message = CONCAT('User has unpaid fines: $', v_unpaid_fines);
        ROLLBACK;
    ELSEIF v_copy_id IS NULL THEN
        SET p_success = FALSE;
        SET p_message = 'Book copy not found.';
        ROLLBACK;
    ELSEIF v_copy_status != 'AVAILABLE' THEN
        SET p_success = FALSE;
        SET p_message = 'Book copy is not available.';
        ROLLBACK;
    ELSE
        -- Perform issue
        INSERT INTO book_issues (user_id, copy_id, issue_date, due_date, status)
        VALUES (p_user_id, v_copy_id, CURDATE(), p_due_date, 'ISSUED');
        
        -- Update Copy Status
        UPDATE book_copies SET status = 'ISSUED' WHERE id = v_copy_id;
        
        -- Update Book Available Copies count
        UPDATE books b
        JOIN book_copies bc ON b.id = bc.book_id
        SET b.available_copies = b.available_copies - 1
        WHERE bc.id = v_copy_id;
        
        -- Create audit log
        INSERT INTO audit_logs(user_id, action, details)
        VALUES (p_user_id, 'ISSUE_BOOK', CONCAT('Issued copy barcode: ', p_copy_barcode));
        
        SET p_success = TRUE;
        SET p_message = 'Book issued successfully.';
        COMMIT;
    END IF;
END$$

-- Procedure to process return of a book and auto-assess fine
CREATE PROCEDURE ReturnBookProcess(
    IN p_copy_barcode VARCHAR(50),
    IN p_condition VARCHAR(100),
    OUT p_success BOOLEAN,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_copy_id INT DEFAULT NULL;
    DECLARE v_issue_id INT DEFAULT NULL;
    DECLARE v_user_id INT DEFAULT NULL;
    DECLARE v_due_date DATE;
    DECLARE v_days_late INT DEFAULT 0;
    DECLARE v_fine_amount DECIMAL(10,2) DEFAULT 0.00;
    
    START TRANSACTION;
    
    SELECT id INTO v_copy_id FROM book_copies WHERE barcode = p_copy_barcode;
    
    IF v_copy_id IS NULL THEN
        SET p_success = FALSE;
        SET p_message = 'Book copy not found.';
        ROLLBACK;
    ELSE
        -- Find the active issue
        SELECT id, user_id, due_date INTO v_issue_id, v_user_id, v_due_date
        FROM book_issues
        WHERE copy_id = v_copy_id AND status IN ('ISSUED', 'OVERDUE')
        ORDER BY id DESC LIMIT 1;
        
        IF v_issue_id IS NULL THEN
            SET p_success = FALSE;
            SET p_message = 'No active issue record found for this copy.';
            ROLLBACK;
        ELSE
            -- Update issue record
            UPDATE book_issues 
            SET return_date = CURDATE(), status = 'RETURNED'
            WHERE id = v_issue_id;
            
            -- Reset Copy status
            UPDATE book_copies 
            SET status = 'AVAILABLE', book_condition = p_condition
            WHERE id = v_copy_id;
            
            -- Update book available copies
            UPDATE books b
            JOIN book_copies bc ON b.id = bc.book_id
            SET b.available_copies = b.available_copies + 1
            WHERE bc.id = v_copy_id;
            
            -- Assess fine if late
            SET v_days_late = DATEDIFF(CURDATE(), v_due_date);
            IF v_days_late > 0 THEN
                SET v_fine_amount = v_days_late * 1.50;
                
                INSERT INTO fines (issue_id, amount, status)
                VALUES (v_issue_id, v_fine_amount, 'UNPAID');
                
                INSERT INTO notifications(user_id, message, type)
                VALUES (v_user_id, CONCAT('Late return fine generated: $', v_fine_amount, ' for ', v_days_late, ' days late.'), 'FINE');
            END IF;
            
            INSERT INTO audit_logs(user_id, action, details)
            VALUES (v_user_id, 'RETURN_BOOK', CONCAT('Returned copy: ', p_copy_barcode, '. Fine: $', v_fine_amount));
            
            SET p_success = TRUE;
            SET p_message = 'Book returned successfully.';
            COMMIT;
        END IF;
    END IF;
END$$

DELIMITER ;

-- ==========================================
-- SEED DATA
-- ==========================================

-- Insert Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_ADMIN', 'System Administrator with complete access'),
(2, 'ROLE_LIBRARIAN', 'Librarian who manages books, categories, issues and returns'),
(3, 'ROLE_STUDENT', 'Student borrowing privileges'),
(4, 'ROLE_FACULTY', 'Faculty borrowing privileges');

-- Insert Permissions
INSERT INTO permissions (id, name, description) VALUES
(1, 'MANAGE_USERS', 'Create, update, delete users'),
(2, 'MANAGE_BOOKS', 'Add, edit, remove books and copies'),
(3, 'MANAGE_ISSUES', 'Issue, return and renew book copies'),
(4, 'MANAGE_RESERVATIONS', 'View and process reservations'),
(5, 'VIEW_REPORTS', 'View analytics dashboard and reports'),
(6, 'BORROW_BOOKS', 'Request borrow and view personal catalog');

-- Role-Permissions Mapping
-- Admin permissions (all)
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);
-- Librarian permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(2, 2), (2, 3), (2, 4), (2, 5), (2, 6);
-- Student permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(3, 6);
-- Faculty permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES 
(4, 6);

-- Insert Users (Passwords are BCrypt hashes of 'password123': $2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G)
INSERT INTO users (id, first_name, last_name, email, password_hash, phone, role_id, status) VALUES
(1, 'System', 'Admin', 'admin@library.com', '$2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G', '1234567890', 1, 'ACTIVE'),
(2, 'Alice', 'Librarian', 'librarian@library.com', '$2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G', '2345678901', 2, 'ACTIVE'),
(3, 'John', 'Student', 'student@library.com', '$2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G', '3456789012', 3, 'ACTIVE'),
(4, 'Robert', 'Faculty', 'faculty@library.com', '$2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G', '4567890123', 4, 'ACTIVE'),
(5, 'Jane', 'Smith', 'jane.smith@student.com', '$2a$10$w5Zl0Qd/qD/wYvCq.P7MKeJmG95Q.u7K2bO2sTskY14yB4kZcK12G', '5678901234', 3, 'ACTIVE');

-- Insert Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Computer Science', 'Programming, Algorithms, AI, Database, and Web Technologies'),
(2, 'Mathematics', 'Calculus, Linear Algebra, Statistics, and Discrete Math'),
(3, 'Fiction', 'Novels, Literature, Stories, and Drama'),
(4, 'History', 'World History, Biographies, and Historical Analysis');

-- Insert Authors
INSERT INTO authors (id, name, biography, birth_date) VALUES
(1, 'Robert C. Martin', 'Known as Uncle Bob, author of Clean Code and agile software methodologies expert.', '1952-12-05'),
(2, 'Joshua Bloch', 'Former Software Engineer at Google, Sun Microsystems, designer of Java collections framework.', '1961-08-28'),
(3, 'J.K. Rowling', 'British author, best known for the Harry Potter fantasy novel series.', '1965-07-31'),
(4, 'Thomas H. Cormen', 'Co-author of Introduction to Algorithms, Dartmouth professor.', '1956-06-25');

-- Insert Publishers
INSERT INTO publishers (id, name, address, contact_email) VALUES
(1, 'Prentice Hall', 'Upper Saddle River, New Jersey, USA', 'info@prenticehall.com'),
(2, 'Addison-Wesley', 'Boston, Massachusetts, USA', 'support@addisonwesley.com'),
(3, 'Bloomsbury', 'London, United Kingdom', 'contact@bloomsbury.com'),
(4, 'MIT Press', 'Cambridge, Massachusetts, USA', 'orders@mitpress.com');

-- Insert Books
INSERT INTO books (id, title, isbn, category_id, publisher_id, publication_year, description, cover_image_url, total_copies, available_copies) VALUES
(1, 'Clean Code', '9780132350884', 1, 1, 2008, 'A Handbook of Agile Software Craftsmanship.', 'https://images-na.ssl-images-amazon.com/images/I/41xShfaRxwL._SX379_BO1,204,203,200_.jpg', 5, 4),
(2, 'Effective Java', '9780134685991', 1, 2, 2018, 'A guide to best practices for the Java platform.', 'https://images-na.ssl-images-amazon.com/images/I/41Vp2bH5WpL._SX379_BO1,204,203,200_.jpg', 4, 3),
(3, 'Harry Potter and the Sorcerer''s Stone', '9780439708180', 3, 3, 1998, 'The first novel in the Harry Potter series.', 'https://images-na.ssl-images-amazon.com/images/I/51uO1aI3aSL._SX329_BO1,204,203,200_.jpg', 6, 6),
(4, 'Introduction to Algorithms', '9780262033848', 1, 4, 2009, 'Standard textbook on computer algorithm analysis and design.', 'https://images-na.ssl-images-amazon.com/images/I/41vO1JGoLqL._SX385_BO1,204,203,200_.jpg', 3, 3);

-- Map Books to Authors
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1), -- Clean Code -> Robert C. Martin
(2, 2), -- Effective Java -> Joshua Bloch
(3, 3), -- Harry Potter -> J.K. Rowling
(4, 4); -- Intro to Algorithms -> Thomas Cormen

-- Insert Book Copies
INSERT INTO book_copies (id, book_id, barcode, status, book_condition) VALUES
(1, 1, 'CC-COPY-1', 'ISSUED', 'New'),
(2, 1, 'CC-COPY-2', 'AVAILABLE', 'Good'),
(3, 1, 'CC-COPY-3', 'AVAILABLE', 'Good'),
(4, 1, 'CC-COPY-4', 'AVAILABLE', 'Good'),
(5, 1, 'CC-COPY-5', 'AVAILABLE', 'Good'),
(6, 2, 'EJ-COPY-1', 'ISSUED', 'Good'),
(7, 2, 'EJ-COPY-2', 'AVAILABLE', 'Good'),
(8, 2, 'EJ-COPY-3', 'AVAILABLE', 'Good'),
(9, 2, 'EJ-COPY-4', 'AVAILABLE', 'Fair'),
(10, 3, 'HP-COPY-1', 'AVAILABLE', 'Good'),
(11, 3, 'HP-COPY-2', 'AVAILABLE', 'Good'),
(12, 3, 'HP-COPY-3', 'AVAILABLE', 'New'),
(13, 3, 'HP-COPY-4', 'AVAILABLE', 'Good'),
(14, 3, 'HP-COPY-5', 'AVAILABLE', 'Good'),
(15, 3, 'HP-COPY-6', 'AVAILABLE', 'Good'),
(16, 4, 'AL-COPY-1', 'AVAILABLE', 'Good'),
(17, 4, 'AL-COPY-2', 'AVAILABLE', 'Good'),
(18, 4, 'AL-COPY-3', 'AVAILABLE', 'Good');

-- Insert some issue transactions
INSERT INTO book_issues (id, user_id, copy_id, issue_date, due_date, return_date, status) VALUES
(1, 3, 1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), NULL, 'ISSUED'),
(2, 5, 6, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, 'ISSUED');

-- Generate Overdue fine for issue 2
INSERT INTO fines (id, issue_id, amount, status) VALUES
(1, 2, 9.00, 'UNPAID');

-- Insert seed wishlist items
INSERT INTO wishlists (user_id, book_id) VALUES
(3, 2),
(5, 4);

-- Insert Reviews & Ratings
INSERT INTO book_reviews (book_id, user_id, review_text) VALUES
(1, 3, 'Essential reading for any software engineer. It has completely changed how I write code.'),
(2, 3, 'Java masterclass. Excellent rules and principles.');

INSERT INTO book_ratings (book_id, user_id, rating_value) VALUES
(1, 3, 5),
(2, 3, 5),
(1, 5, 4);

-- Insert initial notifications
INSERT INTO notifications (user_id, message, type) VALUES
(3, 'Welcome to the new Enterprise Library system!', 'GENERAL'),
(5, 'WARNING: Your borrowed book "Effective Java" is 6 days overdue. Please return it as soon as possible.', 'OVERDUE');
