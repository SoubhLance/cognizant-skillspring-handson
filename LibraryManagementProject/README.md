# Enterprise Library Management System (LMS)

This is a complete production-ready **Library Management System** built with a Spring Boot 3.x backend, MySQL database, and React 19 + Vite frontend. It features Role-Based Access Control (RBAC), JWT authentication, transaction-safe book issue/returns, automated fine calculations, background overdue monitoring, and rich dashboard analytics.

---

## 🛠 Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.3.0** (Spring MVC, Spring Security, Spring Data JPA)
- **Hibernate** (JPA Provider)
- **MySQL** (Database)
- **JWT (JJWT)** (Token authentication)
- **BCrypt** (Password encoding)
- **Lombok & MapStruct** (Metadata mapping)
- **OpenAPI Swagger UI** (Swagger documentation)
- **Apache POI & PDFBox** (Reporting and exports)

### Frontend
- **React 19 & TypeScript**
- **Vite** (Build Tool)
- **Tailwind CSS** (Styling)
- **Recharts** (Reporting charts)
- **Lucide Icons** (UI Icons)
- **Axios** (API Client)

### Containerization
- **Docker & Docker Compose**

---

## 📂 Project Structure

```text
LibraryManagementProject/
├── src/
│   ├── main/
│   │   ├── java/com/libraryManagementSystem/
│   │   │   ├── config/          # WebSecurity, CORS, OpenAPI configs
│   │   │   ├── controller/      # REST API Controllers
│   │   │   ├── dto/             # Request/Response Data Transfer Objects
│   │   │   ├── entity/          # JPA Hibernate Entities
│   │   │   ├── enums/           # System State & Role Enums
│   │   │   ├── exception/       # Exceptions & Global Handler
│   │   │   ├── repository/      # JPA Data access layer & Specifications
│   │   │   ├── security/        # JWT utilities, user sessions, filters
│   │   │   ├── service/         # Services & Implementations
│   │   │   ├── mapper/          # MapStruct Mapper interfaces
│   │   │   ├── validator/       # Custom JSR-380 Validators
│   │   │   └── scheduler/       # Cron Overdue & Expiration Schedulers
│   │   └── resources/
│   │       ├── application.yml  # Spring boot configurations
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback.xml      # Console, Audit & Performance logs config
│   │       └── messages.properties
│   └── test/                    # JUnit 5 & Mockito test files
├── frontend/                    # React 19 + TypeScript frontend project
├── database.sql                 # MySQL creation, triggers, stored procedures & seed data
├── docker-compose.yml           # Container orchestration
├── Dockerfile                   # Backend Docker image config
├── postman_collection.json      # Postman API Collection
└── README.md                    # Setup documentation
```

---

## ⚡ Setup & Installation

### 1. Database Initialization
Execute the `database.sql` script inside your MySQL server. This will:
1. Create `library_db`.
2. Generate all tables, foreign keys, and indexes.
3. Install stored procedures (`IssueBookProcess`, `ReturnBookProcess`).
4. Set up triggers.
5. Seed initial roles, permissions, administrative users, categorizations, authors, publishers, books, copies, and loan records.

### 2. Run Backend (Local)
Ensure you have Maven and JDK 21 installed.
```bash
mvn clean package -DskipTests
java -jar target/LibraryManagementProject-0.0.1-SNAPSHOT.jar
```
The server will start at `http://localhost:8080`.

- **Swagger Documentation:** Access endpoints layout at `http://localhost:8080/swagger-ui.html`.

### 3. Run Frontend (Local)
Navigate to the `frontend` folder, install Node modules, and run Vite:
```bash
cd frontend
npm install
npm run dev
```
The app will run at `http://localhost:3000`.

---

## 🐳 Docker Deployment

To launch the complete application stack (MySQL database, Spring Boot backend, and Nginx React frontend) immediately using Docker Compose:
```bash
docker-compose up --build
```
Once healthy, access the React application at `http://localhost:3000` and the backend Swagger panel at `http://localhost:8080/swagger-ui.html`.

---

## 🔐 Default Logins

The database is seeded with credentials (all password hashes decrypt to **`password123`**):
- **Admin Operator:** `admin@library.com`
- **Librarian Operator:** `librarian@library.com`
- **Student Reader:** `student@library.com`
- **Faculty Reader:** `faculty@library.com`
