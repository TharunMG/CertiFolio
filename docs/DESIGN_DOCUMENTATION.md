# CertiFolio: System Design & Architecture Documentation

This document describes the software architecture, data design, interaction models, and security mechanics for **CertiFolio (Student Skill & Certification Registry)**.

---

## 1. Architectural Overview & System Topology

CertiFolio adheres to a **Decoupled Client-Server Architecture**. The user interface operates as an independent Single Page Application (SPA), communicating with a stateless Spring Boot RESTful API service.

```mermaid
graph TD
    subgraph Client Tier ["Client Tier (Browser)"]
        SPA["React 19 SPA (Vite)"]
        Axios["Axios HTTP Client + JWT Interceptor"]
        Router["React Router (Role Guards)"]
    end

    subgraph API Gateway / Security ["API Gateway & Security Layer"]
        CORS["CORS Filter (Port 5173 / 3000)"]
        JWTFilter["JwtAuthenticationFilter"]
        SecurityConfig["Spring Security Filter Chain"]
    end

    subgraph Application Tier ["Application Tier (Spring Boot 3.3 / Java 24)"]
        Controllers["REST Controllers (@RestController)"]
        DTOs["Java 21 Record DTOs"]
        Services["Domain Services (Auth, Student, Faculty, Portfolio)"]
        NIOFile["FileStorageService (NIO File Storage)"]
        OpenPDF["PdfExportService (OpenPDF 2.0)"]
    end

    subgraph Persistence Tier ["Persistence Tier"]
        JPA["Spring Data JPA (Hibernate ORM)"]
        H2["H2 Database / MySQL 8.0 (5 Tables)"]
        Disk["/uploads/certificates/ (PDF / Images)"]
    end

    SPA --> Axios
    Axios --> Router
    Router -->|HTTP JSON / Multipart| CORS
    CORS --> JWTFilter
    JWTFilter --> SecurityConfig
    SecurityConfig --> Controllers
    Controllers --> DTOs
    Controllers --> Services
    Services --> NIOFile
    Services --> OpenPDF
    Services --> JPA
    NIOFile --> Disk
    JPA --> H2
```

---

## 2. Relational Database Design (Entity-Relationship Model)

The persistence layer normalizes data across five core tables to eliminate redundancy while preserving referential integrity:

1. `users`: Stores core credentials, role authorizations, and creation timestamps.
2. `student_profiles`: Stores academic standing (CGPA, year of study, department) and external portfolio links.
3. `skills`: Master catalog of industry technologies across categories (Backend, Frontend, Database, Cloud, Tools).
4. `student_skills`: Associative join table linking students to skills with specific proficiency levels (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`) and proof repository links.
5. `certifications`: Stores metadata for uploaded student certificates, audit remarks, status, and immutable verification timestamps.

```mermaid
erDiagram
    users ||--o| student_profiles : "1 to 1"
    student_profiles ||--o{ student_skills : "1 to Many"
    skills ||--o{ student_skills : "1 to Many"
    student_profiles ||--o{ certifications : "1 to Many"

    users {
        bigint id PK
        varchar email UK "Unique & Not Null"
        varchar password_hash "BCrypt Encrypted"
        varchar role "ROLE_STUDENT, ROLE_FACULTY"
        datetime created_at
    }

    student_profiles {
        bigint id PK
        varchar roll_number UK "Unique Student ID"
        varchar full_name
        varchar department "CSE, IT, ECE, AI&DS"
        int year_of_study
        double cgpa
        varchar github_url
        varchar linkedin_url
        bigint user_id FK "Unique Foreign Key"
    }

    skills {
        bigint id PK
        varchar skill_name UK "e.g. Spring Boot, React"
        varchar category "BACKEND, FRONTEND, DATABASE, CLOUD, TOOLS"
    }

    student_skills {
        bigint id PK
        bigint student_id FK
        bigint skill_id FK
        varchar proficiency "BEGINNER, INTERMEDIATE, ADVANCED"
        varchar project_link "Proof repository URL"
        boolean endorsed "Faculty Endorsement Flag"
    }

    certifications {
        bigint id PK
        bigint student_id FK
        varchar title "Certificate Name"
        varchar issuer "NPTEL, AWS, Coursera"
        varchar credential_id "Verification ID"
        date issue_date
        varchar file_path "Collision-safe UUID Filename"
        varchar status "PENDING, VERIFIED, REJECTED"
        varchar remarks "Faculty Audit Feedback"
        datetime verified_at "Audit Timestamp"
        datetime created_at
    }
```

---

## 3. Domain Model Class Diagram

The following diagram illustrates the relationship between JPA entities, repositories, and services:

```mermaid
classDiagram
    class User {
        -Long id
        -String email
        -String passwordHash
        -Role role
        -LocalDateTime createdAt
        -StudentProfile studentProfile
    }

    class StudentProfile {
        -Long id
        -String rollNumber
        -String fullName
        -String department
        -Integer yearOfStudy
        -Double cgpa
        -String githubUrl
        -String linkedinUrl
        -User user
        -List~StudentSkill~ studentSkills
        -List~Certification~ certifications
    }

    class Skill {
        -Long id
        -String skillName
        -SkillCategory category
    }

    class StudentSkill {
        -Long id
        -StudentProfile studentProfile
        -Skill skill
        -Proficiency proficiency
        -String projectLink
        -Boolean endorsed
    }

    class Certification {
        -Long id
        -StudentProfile studentProfile
        -String title
        -String issuer
        -String credentialId
        -LocalDate issueDate
        -String filePath
        -VerificationStatus status
        -String remarks
        -LocalDateTime verifiedAt
        -LocalDateTime createdAt
    }

    User "1" <--> "1" StudentProfile
    StudentProfile "1" o-- "*" StudentSkill
    Skill "1" o-- "*" StudentSkill
    StudentProfile "1" o-- "*" Certification
```

---

## 4. Sequence Diagrams

### 4.1 Certificate Upload Workflow (Multipart Ingestion)

This diagram details the sequence when a student uploads an academic certificate proof:

```mermaid
sequenceDiagram
    autonumber
    actor Student
    participant UI as React SPA (Client)
    participant SC as StudentController
    participant SS as StudentService
    participant FS as FileStorageService
    participant CR as CertificationRepository
    participant Disk as Local File Storage

    Student->>UI: Selects file (PDF/PNG) & enters title, issuer
    UI->>UI: Validates file type and size (<= 5MB)
    UI->>SC: POST /api/students/{id}/certifications (Multipart)
    SC->>SS: uploadCertificate(id, title, issuer, credId, date, file)
    SS->>FS: storeFile(file, rollNo)
    FS->>FS: Validate MIME type & generate UUID_rollNo.ext
    FS->>Disk: Files.copy(inputStream, targetLocation)
    Disk-->>FS: File Stored
    FS-->>SS: Return unique filename
    SS->>CR: save(Certification with status = PENDING)
    CR-->>SS: Certification Saved
    SS-->>SC: Map to CertificateResponse DTO
    SC-->>UI: 201 Created (JSON Response)
    UI-->>Student: Display Amber "PENDING AUDIT" badge
```

---

### 4.2 Faculty Audit & Verification Workflow

This diagram outlines how faculty members audit proofs and transition the state machine:

```mermaid
sequenceDiagram
    autonumber
    actor Faculty
    participant UI as Faculty Console (React)
    participant FC as FacultyController
    participant FS as FacultyService
    participant CR as CertificationRepository

    Faculty->>UI: Views Pending Verification Queue
    UI->>FC: GET /api/faculty/certifications/pending
    FC->>FS: getPendingCertificates()
    FS->>CR: findByStatusOrderByCreatedAtDesc(PENDING)
    CR-->>FS: Return List<Certification>
    FS-->>FC-->>UI: Display unverified submissions table
    Faculty->>UI: Clicks "Inspect Proof" (Modal Previewer)
    Faculty->>UI: Selects "Approve" & enters audit remarks
    UI->>FC: PATCH /api/faculty/certifications/{id}/verify
    FC->>FS: verifyCertificate(certId, request)
    FS->>CR: findById(certId)
    CR-->>FS: Found Certification
    FS->>FS: cert.setStatus(VERIFIED)
    FS->>FS: cert.setRemarks(remarks)
    FS->>FS: cert.setVerifiedAt(LocalDateTime.now())
    FS->>CR: save(cert)
    CR-->>FS: Persisted
    FS-->>FC-->>UI: 200 OK (Updated DTO)
    UI-->>Faculty: Queue item resolved, status becomes VERIFIED
```

---

### 4.3 Public Portfolio & OpenPDF Generation

This diagram illustrates how public recruiters query credentials and trigger on-the-fly PDF creation:

```mermaid
sequenceDiagram
    autonumber
    actor Recruiter as Recruiter / Evaluator
    participant UI as Public Portfolio Page
    participant PC as PortfolioController
    participant PS as PortfolioService
    participant PDF as PdfExportService
    participant CR as CertificationRepository

    Recruiter->>UI: Accesses /portfolio/23CS042
    UI->>PC: GET /api/portfolio/23CS042
    PC->>PS: getPublicPortfolio("23CS042")
    PS->>CR: findByRollNoAndStatus("23CS042", VERIFIED)
    Note over PS,CR: Architectural Safeguard: SQL-Level Isolation
    CR-->>PS: Return strictly VERIFIED certificates
    PS-->>PC-->>UI: Return PublicPortfolioResponse
    UI-->>Recruiter: Renders verified profile & skills
    Recruiter->>UI: Clicks "Download Official Portfolio PDF"
    UI->>PC: GET /api/portfolio/23CS042/pdf
    PC->>PDF: generatePortfolioPdf("23CS042")
    PDF->>PS: Fetch verified profile
    PS-->>PDF: Return data
    PDF->>PDF: Construct OpenPDF Document (Bio, Skills, Certs Table, Audit Stamp)
    PDF-->>PC: Return byte[] (application/pdf)
    PC-->>UI: Stream PDF with Content-Disposition
    UI-->>Recruiter: Browser initiates PDF download
```

---

## 5. State Machine Diagram: Certification Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PENDING : Student uploads certificate proof (PDF/PNG/JPEG)
    PENDING --> VERIFIED : Faculty inspects proof, approves & submits audit remarks
    PENDING --> REJECTED : Faculty identifies invalid proof & submits rejection notes
    VERIFIED --> [*] : Visible on Public Portfolio & Included in Official PDF
    REJECTED --> [*] : Shielded from Public Portfolio, visible only in student tracker
```

---

## 6. Security Architecture & RBAC

The system employs a stateless security model with Role-Based Access Control (RBAC):

1. **Authentication Flow**:
   - The client submits email/password to `/api/auth/login`.
   - Credentials are confirmed against BCrypt hashes stored in the database.
   - Upon successful verification, a cryptographically signed HMAC-SHA256 JWT is generated with claims (`userId`, `email`, `role`, `exp`).
2. **Request Authorization**:
   - For every subsequent HTTP request, `JwtAuthenticationFilter` intercepts the request header (`Authorization: Bearer <token>`).
   - The token signature and expiration date are validated.
   - A `UsernamePasswordAuthenticationToken` with granted authorities (`ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`) is placed in the Spring `SecurityContextHolder`.
3. **Route Protection**:
   - `/api/students/**`: Accessible to authenticated students.
   - `/api/faculty/**`: Restricted to authenticated faculty and administrators.
   - `/api/portfolio/**`: Publicly accessible to permit external portfolio sharing.

---

## 7. Architectural Safeguards (Viva-Voce Standout Design)

Evaluators frequently ask how CertiFolio guarantees against credential falsification. The system implements two architectural safeguards:

1. **SQL-Level Query Isolation**:
   The public portfolio does not rely on frontend filtering. Instead, the backend repository executes:
   ```sql
   SELECT c FROM Certification c 
   WHERE c.studentProfile.rollNumber = :rollNo 
     AND c.status = 'VERIFIED'
   ```
   Unverified (`PENDING`) or fraudulent (`REJECTED`) submissions are never serialized or transmitted across the network to public clients.

2. **Immutable Audit Timestamping**:
   Every approved certificate permanently records:
   - The faculty auditor's remarks.
   - The exact timestamp (`verified_at`) recorded by the institutional server.
   - A digital certificate hash reference embedded in the OpenPDF transcript.
