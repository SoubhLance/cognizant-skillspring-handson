# 📚 Enterprise Library Management System (LMS)

A complete, production-ready, enterprise-grade **Library Management System** built with a robust Java 21 & Spring Boot 3.x backend, a transaction-safe MySQL database engine, and a modern, high-performance React 19 + TypeScript + Vite frontend.

This project is fully structured, modularized, and designed to look and function like commercial enterprise software.

---

## 🌟 Key Features

- **🔐 Robust Security & RBAC:** Secures endpoints using JWT-based stateless authentication, BCrypt hashing, and method-level access controls for 4 roles: `ADMIN`, `LIBRARIAN`, `STUDENT`, and `FACULTY`.
- **🔄 Transaction-Safe Loans:** Implements MySQL stored procedures to automate issue-return logic, adjust copy availability metrics, check borrower block status, and calculate late fee fines.
- **📊 Interactive Analytics Dashboard:** Responsive layouts containing Recharts data visualization grids tracking inventory stock, active borrow rates, fine collections, and administrator audit trails.
- **📄 Advanced Document Exporters:** Built-in generators to export database catalog listings and transaction archives into CSV, Excel, and PDF files.
- **📥 CSV Bulk Importer:** Admin tool to ingest CSV catalog files and automatically map authors, genres, publishers, and barcodes.
- **⏰ Automated Background Tasks:** Spring Schedulers executing daily cron loops to flag overdue book issues and cancel stale hold reservations automatically.
- **🔎 Dynamic Advanced Search:** Book query engine featuring multiple filter parameters, sorting constraints, and query auto-suggestions.

---

## 🛠 Technology Stack

### Backend
- **Core:** Java 21, Spring Boot 3.3.0 (MVC, Data JPA, Security)
- **Database:** MySQL 8.0, Hibernate ORM
- **Security:** JSON Web Tokens (JJWT), BCrypt Password Encoder
- **Utilities:** MapStruct, Project Lombok, OpenAPI Swagger-UI, Logback, Apache POI, Apache PDFBox
- **Testing:** JUnit 5, Mockito

### Frontend
- **Core:** React 19, TypeScript, Vite
- **Styling:** Tailwind CSS v3, Framer Motion
- **Libraries:** Recharts, Lucide Icons, Axios, React Router DOM, React Query

---

## 📂 Project Directory Structure

```text
LibraryManagementProject/
├── src/
│   ├── main/
│   │   ├── java/com/libraryManagementSystem/
│   │   │   ├── config/          # Spring Security, CORS, OpenAPI Swagger
│   │   │   ├── controller/      # REST API Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Hibernate Model Mapping
│   │   │   ├── enums/           # System States (UserStatus, BookCopyStatus, etc)
│   │   │   ├── exception/       # Exception definitions & Global Handler
│   │   │   ├── repository/      # Spring Data Access, Custom JPQL & Specifications
│   │   │   ├── security/        # JWT generators, filters, and UserDetails
│   │   │   ├── service/         # Service contracts and Business Logics
│   │   │   ├── mapper/          # MapStruct Mapper interfaces
│   │   │   ├── validator/       # Custom validators for ISBN, Phone, & Passwords
│   │   │   └── scheduler/       # Late fine & hold expiration schedulers
│   │   └── resources/
│   │       ├── application.yml  # System settings
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback.xml      # Rolling File and console logging
│   │       └── messages.properties
│   └── test/                    # JUnit 5 & Mockito test cases
├── frontend/                    # Vite + React 19 Client Web App
├── database.sql                 # Complete DDL schemas, triggers, procedures, & seeds
├── docker-compose.yml           # MySQL, Backend & Frontend Docker configs
├── Dockerfile                   # Java 21 JRE packaging Dockerfile
├── postman_collection.json      # Postman REST testing endpoints
└── README.md                    # Module documentation
```

---

## 🚀 Getting Started

### Method A: Docker Compose Deployment (Recommended)
You can build and deploy the entire multi-container stack (MySQL Database + Backend Server + Frontend Web client) in one command:
```bash
docker-compose up --build
```
- **React Web Client:** Access at [http://localhost:3000](http://localhost:3000)
- **Swagger Documentation:** Access at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **MySQL Database:** Local link at port `3306`

*Note: The MySQL container automatically imports `database.sql` to initialize database structures and populate all seed data.*

### Method B: Manual Local Setup
1. **Initialize MySQL Database:**
   - Log into your MySQL Server.
   - Run the script: `database.sql`. This generates tables, constraints, stored procedures, triggers, and all test users.
2. **Build and Run Backend:**
   Ensure you have JDK 21 and Maven. Run from the `LibraryManagementProject/` folder:
   ```bash
   mvn clean package -DskipTests
   java -jar target/LibraryManagementProject-0.0.1-SNAPSHOT.jar
   ```
3. **Run Frontend Client:**
   Ensure you have Node.js. Navigate to the `LibraryManagementProject/frontend` folder:
   ```bash
   npm install
   npm run dev
   ```

---

## 🔐 Default Access Logins (Password: `password123`)

For immediate evaluation and demonstration, the database includes four pre-populated logins representing each system role:
- **System Admin (Access all options, view audit trails):** `admin@library.com`
- **Librarian (Checkout, check-in copies, waived fees):** `librarian@library.com`
- **Student Account (Browse catalog, request reservations, view fines):** `student@library.com`
- **Faculty Account (Higher loan counts and borrowing periods):** `faculty@library.com`
