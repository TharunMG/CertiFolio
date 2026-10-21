# 💻 CertiFolio - Code-Level Documentation

This document provides class-by-class and method-by-method documentation for developers working on the **CertiFolio** codebase.

---

## 📑 Table of Contents
1. [Project Directory & Package Hierarchy](#1-project-directory--package-hierarchy)
2. [Domain Layer (`com.certifolio.model`)](#2-domain-layer-comcertifoliomodel)
3. [Repository Layer (`com.certifolio.repository`)](#3-repository-layer-comcertifoliorepository)
4. [Service Layer (`com.certifolio.service`)](#4-service-layer-comcertifolioservice)
5. [DTO & Validation Layer (`com.certifolio.dto`)](#5-dto--validation-layer-comcertifoliodto)
6. [Controller Layer (`com.certifolio.controller`)](#6-controller-layer-comcertifoliocontroller)
   - [REST API Controllers (`controller.api`)](#61-rest-api-controllers)
   - [Web MVC Controllers (`controller.web`)](#62-web-mvc-controllers)
7. [Exception Handling (`com.certifolio.exception`)](#7-exception-handling-comcertifolioexception)
8. [Configuration & Bootstrapping (`com.certifolio.config`)](#8-configuration--bootstrapping-comcertifolioconfig)
9. [Automated Testing Suite (`src/test/java`)](#9-automated-testing-suite)
10. [Developer Extension Guide](#10-developer-extension-guide)

---

## 1. Project Directory & Package Hierarchy

```
src/main/java/com/certifolio/
├── CertifolioApplication.java          # Spring Boot main entry point
├── config/
│   ├── DataInitializer.java           # CommandLineRunner seeding initial data
│   └── OpenApiConfig.java             # OpenAPI 3 / Swagger metadata configuration
├── controller/
│   ├── api/                           # Headless REST API endpoints (/api/v1/*)
│   │   ├── AnalyticsApiController.java
│   │   ├── CertificationApiController.java
│   │   ├── SkillApiController.java
│   │   └── StudentApiController.java
│   └── web/                           # Thymeleaf web page controllers
│       ├── AnalyticsWebController.java
│       ├── CertificationWebController.java
│       ├── HomeController.java
│       ├── SkillWebController.java
│       ├── StudentWebController.java
│       └── VerificationWebController.java
├── dto/                               # Request/Response Data Transfer Objects
│   ├── AnalyticsDto.java
│   ├── CertificationDto.java
│   ├── SkillDto.java
│   ├── StudentDto.java
│   └── VerificationDecisionDto.java
├── exception/                         # Centralized error handling
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── model/                             # JPA Entities and Enums
│   ├── Certification.java
│   ├── ProficiencyLevel.java          # ENUM
│   ├── Skill.java
│   ├── SkillCategory.java             # ENUM
│   ├── Student.java
│   └── VerificationStatus.java        # ENUM
├── repository/                        # Spring Data JPA Repositories
│   ├── CertificationRepository.java
│   ├── SkillRepository.java
│   └── StudentRepository.java
└── service/                           # Business logic services
    ├── AnalyticsService.java
    ├── CertificationService.java
    ├── SkillService.java
    └── StudentService.java
```

---

## 2. Domain Layer (`com.certifolio.model`)

### 2.1 `Student.java`
Represents a student enrolled in the institution who manages their skills and certifications.

- **Annotations**: `@Entity`, `@Table(name = "students")`
- **Fields**:
  - `Long id`: Primary key (Identity auto-generated).
  - `String regNo`: Unique registration number (e.g., `REG2023CS0101`). Unique index enforced.
  - `String fullName`: Full name of the student.
  - `String email`: Email address. Unique index enforced.
  - `String department`: Department/Branch (e.g. `Computer Science & Engineering`).
  - `Integer yearOfStudy`: Academic year (1 through 4).
  - `Double cgpa`: Academic cumulative grade point average (out of 10.0).
  - `String bio`: Personal summary / career headline.
  - `String githubUrl`: URL to public GitHub profile.
  - `String linkedinUrl`: URL to public LinkedIn profile.
  - `LocalDateTime createdAt`: Auto-generated timestamp upon registration.
  - `List<Skill> skills`: One-to-Many association to `Skill`, cascaded with orphan removal.
  - `List<Certification> certifications`: One-to-Many association to `Certification`.
- **Helper Methods**:
  - `addSkill(Skill skill)` / `removeSkill(Skill skill)`: Synchronizes bi-directional relationship.
  - `addCertification(Certification cert)` / `removeCertification(Certification cert)`: Synchronizes bi-directional relationship.
  - `getVerifiedCertificationsCount()`: Calculates count of certifications with `VerificationStatus.VERIFIED`.
  - `getPendingCertificationsCount()`: Calculates count of certifications awaiting review.

### 2.2 `Skill.java`
Represents an individual technical competency associated with a student.

- **Annotations**: `@Entity`, `@Table(name = "skills")`
- **Fields**:
  - `Long id`: Primary key.
  - `String name`: Name of the skill (e.g. `Java`, `AWS`, `Python`).
  - `SkillCategory category`: Enum grouping (e.g., `PROGRAMMING_LANGUAGE`, `CLOUD_DEVOPS`).
  - `ProficiencyLevel proficiencyLevel`: Enum (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`).
  - `Integer rating`: Self-assessed numeric rating between 1 and 5.
  - `Student student`: Many-to-One association back to the owning `Student`.

### 2.3 `Certification.java`
Represents an accredited certification achieved by a student.

- **Annotations**: `@Entity`, `@Table(name = "certifications")`
- **Fields**:
  - `Long id`: Primary key.
  - `String title`: Title of the credential (e.g. `AWS Solutions Architect - Associate`).
  - `String issuingOrganization`: Issuer (e.g. `Amazon Web Services`, `Google Cloud`).
  - `LocalDate issueDate`: Date of credential issuance.
  - `LocalDate expiryDate`: Date of expiration (nullable if lifetime/does not expire).
  - `String credentialId`: License or credential verification ID.
  - `String credentialUrl`: Public web link for verification proof.
  - `VerificationStatus verificationStatus`: `PENDING`, `VERIFIED`, or `REJECTED`.
  - `String verifierNotes`: Feedback notes recorded by the faculty verifier.
  - `String verifiedBy`: Name/Title of the faculty verifier who reviewed the credential.
  - `LocalDateTime verifiedAt`: Timestamp when verification was completed.
  - `Student student`: Many-to-One association back to `Student`.
- **Helper Method**:
  - `isExpired()`: Returns `true` if `expiryDate` is present and precedes the current date.

### 2.4 Domain Enums
- **`ProficiencyLevel`**: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`.
- **`SkillCategory`**:
  - `PROGRAMMING_LANGUAGE` ("Programming Language")
  - `FRAMEWORK_LIBRARY` ("Framework & Library")
  - `CLOUD_DEVOPS` ("Cloud & DevOps")
  - `DATA_AI` ("Data Science & AI")
  - `CYBERSECURITY` ("Cybersecurity")
  - `TOOLS_PLATFORMS` ("Tools & Platforms")
  - `SOFT_SKILLS` ("Professional & Soft Skills")
- **`VerificationStatus`**:
  - `PENDING` (badgeColor: "warning")
  - `VERIFIED` (badgeColor: "success")
  - `REJECTED` (badgeColor: "danger")

---

## 3. Repository Layer (`com.certifolio.repository`)

### 3.1 `StudentRepository`
Extends `JpaRepository<Student, Long>`:
- `Optional<Student> findByRegNo(String regNo)`
- `Optional<Student> findByEmail(String email)`
- `boolean existsByRegNo(String regNo)`
- `boolean existsByEmail(String email)`
- `List<Student> findByDepartmentIgnoreCase(String department)`
- `@Query("SELECT s FROM Student s WHERE ...") List<Student> searchStudents(@Param("query") String query)`: Multi-field search across student full name, registration number, department, and email.
- `@Query("SELECT DISTINCT s FROM Student s JOIN s.skills sk WHERE LOWER(sk.name) LIKE ...") List<Student> findStudentsBySkillName(String skillName)`
- `@Query("SELECT DISTINCT s.department FROM Student s ORDER BY s.department ASC") List<String> findAllDepartments()`

### 3.2 `SkillRepository`
Extends `JpaRepository<Skill, Long>`:
- `List<Skill> findByStudentId(Long studentId)`
- `List<Skill> findByCategory(SkillCategory category)`
- `@Query("SELECT s.name, COUNT(s) as cnt FROM Skill s GROUP BY s.name ORDER BY cnt DESC") List<Object[]> findTopSkillsRaw()`
- `@Query("SELECT s.category, COUNT(s) FROM Skill s GROUP BY s.category") List<Object[]> countSkillsByCategoryRaw()`
- `@Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE ...") List<Skill> searchSkillsByKeyword(String keyword)`

### 3.3 `CertificationRepository`
Extends `JpaRepository<Certification, Long>`:
- `List<Certification> findByStudentId(Long studentId)`
- `List<Certification> findByVerificationStatusOrderByIssueDateDesc(VerificationStatus status)`
- `long countByVerificationStatus(VerificationStatus status)`
- `@Query("SELECT c.issuingOrganization, COUNT(c) FROM Certification c GROUP BY c.issuingOrganization ORDER BY COUNT(c) DESC") List<Object[]> countCertificationsByIssuerRaw()`
- `@Query("SELECT c FROM Certification c WHERE ...") List<Certification> searchCertifications(String keyword)`

---

## 4. Service Layer (`com.certifolio.service`)

All service classes are annotated with `@Service` and manage transactional boundaries via `@Transactional`.

### 4.1 `StudentService`
- `getAllStudents()`: Retrieves all student records.
- `getStudentById(Long id)`: Retrieves a student or throws `ResourceNotFoundException`.
- `getStudentByRegNo(String regNo)`: Look up by registration number.
- `searchStudents(String query)`: Executes search filter.
- `findByDepartment(String department)`: Filter by academic department.
- `findStudentsBySkill(String skillName)`: Searches students with a matching skill.
- `createStudent(StudentDto dto)`: Validates uniqueness of `regNo` and `email` before persisting.
- `updateStudent(Long id, StudentDto dto)`: Verifies uniqueness on modified fields and updates profile.
- `deleteStudent(Long id)`: Cascades deletion of student and child records.

### 4.2 `SkillService`
- `getAllSkills()` / `getSkillsByStudentId(Long studentId)` / `getSkillsByCategory(SkillCategory category)`
- `addSkillToStudent(SkillDto dto)`: Resolves owning `Student` entity and associates new `Skill`.
- `updateSkill(Long id, SkillDto dto)`: Updates skill name, proficiency, and star rating.
- `deleteSkill(Long id)`: Removes skill from student.

### 4.3 `CertificationService`
- `getAllCertifications()` / `getCertificationsByStatus(VerificationStatus status)`
- `getPendingCertifications()`: Returns all certifications awaiting audit review.
- `addCertification(CertificationDto dto)`: Creates a certification initialized in `PENDING` status.
- `verifyCertification(Long id, VerificationDecisionDto decision)`:
  - Updates `verificationStatus` to `VERIFIED` or `REJECTED`.
  - Stamps `verifiedBy`, `verifierNotes`, and sets `verifiedAt = LocalDateTime.now()`.
- `deleteCertification(Long id)`: Removes certification record.

### 4.4 `AnalyticsService`
- `getAnalyticsSummary()`: Computes registry-wide insights:
  - Total students, total certifications, verified, pending, rejected counts.
  - Calculates `verificationRatePercentage` with 1 decimal precision.
  - Aggregates top skills and count of certifications per issuing organization.

---

## 5. DTO & Validation Layer (`com.certifolio.dto`)

DTOs isolate internal database representations from external user inputs:

| DTO Class | Primary Validations |
|---|---|
| `StudentDto` | `@NotBlank regNo`, `@NotBlank fullName`, `@Email email`, `@NotBlank department`, `@NotNull yearOfStudy` |
| `SkillDto` | `@NotBlank name`, `@NotNull category`, `@NotNull proficiencyLevel`, `@Min(1) @Max(5) rating`, `@NotNull studentId` |
| `CertificationDto` | `@NotBlank title`, `@NotBlank issuingOrganization`, `@NotNull issueDate`, `@NotNull studentId` |
| `VerificationDecisionDto`| `@NotNull status` (`VERIFIED` or `REJECTED`), `verifierNotes`, `verifiedBy` |
| `AnalyticsDto` | Aggregated report payload for metrics and Chart.js feeds |

---

## 6. Controller Layer (`com.certifolio.controller`)

### 6.1 REST API Controllers
- **`StudentApiController` (`/api/v1/students`)**: JSON CRUD endpoints with OpenAPI annotations.
- **`SkillApiController` (`/api/v1/skills`)**: JSON endpoints for skill addition, update, and search.
- **`CertificationApiController` (`/api/v1/certifications`)**: Certification submission and verification PATCH endpoint.
- **`AnalyticsApiController` (`/api/v1/analytics`)**: Returns JSON analytics summary at `/api/v1/analytics/summary`.

### 6.2 Web MVC Controllers
- **`HomeController` (`/`)**: Renders home overview with metrics and pending audit notifications.
- **`StudentWebController` (`/students`)**: Handles student directory, create/edit forms, and public portfolio view (`/students/{id}/portfolio`).
- **`CertificationWebController` (`/certifications`)**: Lists all certifications and provides submission form.
- **`SkillWebController` (`/skills`)**: Renders the institution-wide Skill Matrix.
- **`VerificationWebController` (`/audit`)**: Faculty console for reviewing and stamping pending credentials.
- **`AnalyticsWebController` (`/analytics`)**: Renders Chart.js visualizations.

---

## 7. Exception Handling (`com.certifolio.exception`)

### `GlobalExceptionHandler.java`
Annotated with `@ControllerAdvice`:
- **`ResourceNotFoundException`**: Returns HTTP `404 Not Found` with timestamp and error message.
- **`MethodArgumentNotValidException`**: Intercepts Jakarta validation failures and returns HTTP `400 Bad Request` with field-by-field error mappings.
- **`IllegalArgumentException`**: Handles duplicate registration numbers or business rule violations, returning HTTP `400 Bad Request`.

---

## 8. Configuration & Bootstrapping (`com.certifolio.config`)

### `DataInitializer.java`
Implements Spring Boot's `CommandLineRunner`:
- Runs automatically on application startup.
- Verifies if the student table is empty.
- Seeds 5 diverse students across Computer Science, AI & DS, IT, Cybersecurity, and Electronics.
- Populates industry certifications from AWS, Oracle, Google Cloud, Coursera, Meta, and Cisco with a mix of `VERIFIED`, `PENDING`, and `REJECTED` states for immediate testing.

### `OpenApiConfig.java`
Configures OpenAPI 3 metadata, title, contact information, and license details displayed on Swagger UI at `/swagger-ui.html`.

---

## 9. Automated Testing Suite

Located under `src/test/java/com/certifolio/`:

1. **`CertifolioApplicationTests.java`**:
   - Asserts that the Spring application context initializes without configuration faults.
2. **`StudentServiceTest.java`**:
   - Tests student creation, entity persistence, retrieval, and enforcement of duplicate registration number constraints.
3. **`CertificationServiceTest.java`**:
   - Validates the complete certification lifecycle: submission in `PENDING` state, followed by verification into `VERIFIED` state with verifier notes and timestamps.
4. **`StudentApiControllerTest.java`**:
   - Uses `MockMvc` to test that REST endpoints (`/api/v1/students` and `/api/v1/analytics/summary`) return HTTP `200 OK` with valid JSON content types and schema.

Run all tests via:
```powershell
.\mvnw.cmd test
```

---

## 10. Developer Extension Guide

### Adding a New Skill Category
1. Open `SkillCategory.java`.
2. Add the new enum constant with its display name:
   ```java
   MOBILE_DEVELOPMENT("Mobile App Development")
   ```
3. The UI forms and matrix will automatically render the new category via `SkillCategory.values()`.

### Adding File Upload for Certificates
1. In `Certification.java`, add a column:
   ```java
   @Column(length = 255)
   private String documentPath;
   ```
2. In `CertificationDto.java`, accept `MultipartFile certificateFile`.
3. Save the file in a local storage directory or S3 bucket within `CertificationService`.
