# Student Management System

A REST API for managing students, subjects, and student–subject assignments, built with Spring Boot, Spring Data JPA, and MySQL.

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.1.1 (Spring Web, Spring Data JPA, Spring Validation)
- **Database:** MySQL 8+
- **ORM:** Hibernate (via Spring Data JPA)
- **Testing:** JUnit 5, Mockito
- **Build tool:** Maven

## Architecture

The application follows a standard layered architecture, with one vertical slice per entity in the schema:

```
Client (Postman / curl)
        |
Controller layer   -> HTTP request/response, path binding, @Valid input checks
        |
Service layer      -> business rules (duplicate checks, existence checks,
        |              assignment rules), one @Service per entity
        |
Repository layer   -> Spring Data JPA interfaces, no custom logic
        |
MySQL database      -> tables + constraints as a backstop for data integrity
```

Business validation lives entirely in the Service layer. Controllers only handle
HTTP concerns; Repositories only handle persistence. This separation is what
makes the Service layer unit-testable in isolation with Mockito, without a
running database.

`StudentSubject` (the student–subject assignment) is modeled as its own entity,
service, and controller — not as an implicit `@ManyToMany` — because the
junction table carries its own data (`assignedAt`), which `@ManyToMany` cannot
represent.

## Database Schema

Three tables: `student`, `subject`, and `student_subject` (the join table).

```sql
CREATE DATABASE studentmanagement;

CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    age INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_subject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subject(id) ON DELETE RESTRICT
);
```

Notes on the foreign keys:
- `student_id` uses `ON DELETE CASCADE` — an assignment row has no meaning once
  the student it belongs to is gone.
- `subject_id` uses `ON DELETE RESTRICT` — this is the database-level backstop
  for the rule "a subject that's in use cannot be removed." The actual
  enforcement happens in the Service layer (see below); this constraint exists
  in case that check is ever bypassed.
- The composite `UNIQUE (student_id, subject_id)` prevents the same subject
  being assigned twice to the same student.

`spring.jpa.hibernate.ddl-auto=validate` is set deliberately: the database is
the source of truth (created from this DDL), and Hibernate only verifies the
JPA entities match it at startup — it never generates or alters schema.

## Setup

### 1. Create the database and a dedicated user

```bash
mysql -u root -p
```
```sql
CREATE DATABASE studentmanagement;
CREATE USER 'student_management'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON studentmanagement.* TO 'student_management'@'localhost';
FLUSH PRIVILEGES;
```

Then run the three `CREATE TABLE` statements above against `studentmanagement`.

### 2. Set the database password as an environment variable

`application.properties` reads the password from `${DB_PASSWORD}` rather than
storing it in plain text. Set it in your shell, or in IntelliJ under
**Run → Edit Configurations → Environment variables**:

```bash
export DB_PASSWORD=yourpassword
```

### 3. Build and run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## API Reference

All endpoints return JSON. Base path has no version/API prefix.

| Method | Endpoint | Description | Success | Errors |
|---|---|---|---|---|
| POST | `/students` | Add a new student | 201 Created | 400, 409 |
| PUT | `/students/{id}` | Update a student | 200 OK | 400, 404, 409 |
| GET | `/students` | List all students, each with their assigned subjects | 200 OK | — |
| POST | `/subjects` | Add a new subject | 201 Created | 400, 409 |
| DELETE | `/subjects/{id}` | Delete a subject (rejected if assigned to any student) | 204 No Content | 404, 409 |
| GET | `/subjects` | List all subjects *(beyond spec — see below)* | 200 OK | — |
| POST | `/students/{studentId}/subjects` | Assign a subject to a student | 201 Created | 400, 404, 409 |

### Example requests

**Add a student**
```bash
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{"name": "Aarav Sharma", "email": "aarav@example.com", "age": 20}'
```

**Add a subject**
```bash
curl -X POST http://localhost:8080/subjects \
  -H "Content-Type: application/json" \
  -d '{"name": "Mathematics", "code": "MATH101"}'
```

**Assign a subject to a student**
```bash
curl -X POST http://localhost:8080/students/1/subjects \
  -H "Content-Type: application/json" \
  -d '{"subjectId": 1}'
```

**Fetch all students with their subjects**
```bash
curl http://localhost:8080/students
```

### Error responses

Errors are returned via a global `@RestControllerAdvice`, mapping domain
exceptions to HTTP status codes rather than leaking stack traces:

| Exception | Status | Thrown when |
|---|---|---|
| `MethodArgumentNotValidException` (built-in) | 400 | A `@Valid` field constraint fails (e.g. blank name, invalid email) |
| `ResourceNotFoundException` | 404 | Student or subject ID doesn't exist |
| `DuplicateResourceException` | 409 | Duplicate email / subject name / subject code / duplicate assignment |
| `SubjectAssignedException` | 409 | Deleting a subject that's currently assigned to a student |

## Design Decisions & Assumptions

- **`GET /subjects` is beyond the original spec.** Without a client, there was
  no way to discover existing subject IDs to use when assigning a subject to a
  student. This is a read-only addition with no business logic, added purely
  for API usability.
- **Hard delete, not soft delete, for subjects.** A soft-delete (`status`
  flag) was considered for auditability, but the spec explicitly asks for
  deletion, so the literal requirement was implemented — guarded by the
  assignment check in the Service layer.
- **No Service-layer interfaces.** Each Service has exactly one
  implementation and Mockito can mock concrete classes directly, so an
  interface per Service was judged to be unnecessary indirection at this
  scale.
- **`assignSubject` lives in its own `StudentSubjectService` /
  `StudentSubjectController`**, mirroring the `student_subject` junction
  table as a first-class entity rather than bolting the operation onto
  `StudentService` or `SubjectService`.
- **Out of scope:** unassigning a subject from a student, fetching a single
  student's details, deleting a student, and soft-delete for subjects. These
  were deliberately excluded to match the stated requirements; noted here so
  the omission reads as a scoping decision rather than an oversight.

## Testing

Unit tests target the Service layer, since that's where all business logic
lives — Repository interfaces are Spring Data JPA-generated with no custom
logic to test, and DTOs/entities are plain data holders.

Each test mocks its repositories with Mockito (`@Mock` / `@InjectMocks`) so
no real database is touched. Tests cover both the rejection path (exception
thrown, and the destructive repository call verified as *not* having
happened via `verify(..., never())`) and the success path for each rule:

- `StudentServiceTest` — duplicate email on create, not-found on update, and
  the nested student→subjects mapping used by `GET /students`
- `SubjectServiceTest` — duplicate name/code on create, not-found on delete,
  and the core FR4 rule: deletion rejected when assigned, allowed when not
- `StudentSubjectServiceTest` — not-found for missing student/subject,
  duplicate-assignment rejection, and successful assignment

Run all tests:
```bash
./mvnw test
```

## Project Structure

```
src/main/java/org/example/studentmanagementsystem/
├── controller/    REST endpoints (StudentController, SubjectController, StudentSubjectController)
├── service/       Business logic and validation, one class per entity
├── repository/    Spring Data JPA interfaces
├── entity/        JPA entities (Student, Subject, StudentSubject)
├── domain/        Request/response DTOs
└── exception/     Custom exceptions + GlobalExceptionHandler
```
