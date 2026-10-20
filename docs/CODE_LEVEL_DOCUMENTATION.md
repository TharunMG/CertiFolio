# CertiFolio: Code-Level Architecture Documentation

This document provides an exhaustive, class-by-class and component-by-component technical breakdown of the **CertiFolio** full-stack implementation.

---

## 1. Directory Structure & Source Tree

```
CertiFolio/
├── backend/
│   ├── pom.xml                                   # Build manifest & dependencies
│   ├── uploads/certificates/                     # Local file storage for certificate proofs
│   └── src/
│       ├── main/
│       │   ├── java/com/college/certifolio/
│       │   │   ├── CertiFolioApplication.java   # Main Spring Boot launcher
│       │   │   ├── config/                      # Security & JWT configuration
│       │   │   ├── controller/                  # REST API controllers
│       │   │   ├── dto/                         # Java 21 Record DTOs
│       │   │   ├── entity/                      # JPA entities
│       │   │   ├── enums/                       # Domain enumerations
│       │   │   ├── exception/                   # Exception handling & advice
│       │   │   ├── repository/                  # Spring Data JPA repositories
│       │   │   └── service/                     # Business services & exporters
│       │   └── resources/
│       │       ├── application.properties       # H2 persistence & server config
│       │       └── application-mysql.properties # MySQL production profile
│       └── test/
│           └── java/com/college/certifolio/     # JUnit 5 automated test suite
├── frontend/
│   ├── package.json                             # React 19, Tailwind, Vite dependencies
│   ├── vite.config.js                           # Vite dev server & proxy settings
│   ├── tailwind.config.js                       # Tailwind custom styling & theme tokens
│   ├── postcss.config.js
│   ├── index.html                               # HTML entry point
│   └── src/
│       ├── main.jsx                             # React DOM entry point
│       ├── App.jsx                              # Route definitions & protected routes
│       ├── index.css                            # Tailwind CSS directives
│       ├── api/axiosConfig.js                   # Axios client with JWT interceptor
│       ├── context/AuthContext.jsx              # Global authentication context
│       ├── components/                          # Reusable UI components
├── docs/                                        # Formal project documentation
└── README.md                                    # Project overview & quickstart
```

---

## 2. Backend Architecture Deep Dive

### 2.1 Configuration Layer (`com.college.certifolio.config`)

#### 1. [`SecurityConfig.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/config/SecurityConfig.java)
- **Role**: Configures the Spring Security Filter Chain.
- **Key Details**:
  - Sets `SessionCreationPolicy.STATELESS` since authentication relies purely on JWT Bearer tokens.
  - Disables CSRF (not required for stateless token architectures).
  - Disables HTTP `frameOptions` to allow in-browser iframe rendering of uploaded PDF certificate proofs and the H2 web console.
  - Registers [`JwtAuthenticationFilter`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/config/JwtAuthenticationFilter.java) prior to `UsernamePasswordAuthenticationFilter`.
  - Configures CORS for `http://localhost:5173` and `http://127.0.0.1:5173`.

#### 2. [`JwtTokenProvider.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/config/JwtTokenProvider.java)
- **Role**: Cryptographic generation and validation of JSON Web Tokens.
- **Key Details**:
  - Uses JJWT `0.12.6` with HMAC-SHA256 secret keys (`Keys.hmacShaKeyFor`).
  - Claims embedded: `subject` (email), `userId`, `role`.
  - Expiration: Configurable via `app.jwt.expiration-ms` (default 24 hours).
  - Methods: `generateToken()`, `getEmailFromToken()`, `getRoleFromToken()`, `validateToken()`.

#### 3. [`JwtAuthenticationFilter.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/config/JwtAuthenticationFilter.java)
- **Role**: `OncePerRequestFilter` inspecting every inbound HTTP request.
- **Key Details**:
  - Parses the `Authorization: Bearer <token>` header.
  - If valid, populates a `UsernamePasswordAuthenticationToken` with granted authority (e.g. `ROLE_STUDENT` or `ROLE_FACULTY`) and injects it into `SecurityContextHolder`.

---

### 2.2 Domain Entities (`com.college.certifolio.entity`)

Written in standard modern Java with explicit encapsulation and builder patterns:

1. **[`User.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/entity/User.java)**:
   - Table: `users`.
   - Fields: `id` (PK), `email` (Unique), `passwordHash`, `role` (`ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`), `createdAt`.
   - Relationship: `@OneToOne(mappedBy = "user")` with `StudentProfile`.

2. **[`StudentProfile.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/entity/StudentProfile.java)**:
   - Table: `student_profiles`.
   - Fields: `id` (PK), `rollNumber` (Unique, e.g., `23CS042`), `fullName`, `department` (e.g., `CSE`), `yearOfStudy`, `cgpa`, `githubUrl`, `linkedinUrl`.
   - Relationships: `@OneToOne` with `User`, `@OneToMany` with `StudentSkill`, `@OneToMany` with `Certification`.

3. **[`Skill.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/entity/Skill.java)**:
   - Table: `skills`.
   - Fields: `id` (PK), `skillName` (Unique), `category` (`BACKEND`, `FRONTEND`, `DATABASE`, `CLOUD`, `TOOLS`).

4. **[`StudentSkill.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/entity/StudentSkill.java)**:
   - Table: `student_skills`.
   - Unique Constraint: `(student_id, skill_id)` prevents assigning duplicate skills to the same profile.
   - Fields: `proficiency` (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), `projectLink`, `endorsed` (Boolean).

5. **[`Certification.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/entity/Certification.java)**:
   - Table: `certifications`.
   - Fields: `title`, `issuer`, `credentialId`, `issueDate`, `filePath`, `status` (`PENDING`, `VERIFIED`, `REJECTED`), `remarks`, `verifiedAt`, `createdAt`.
   - `@PrePersist`: Defaults status to `PENDING` and stamps `createdAt`.

---

### 2.3 Java 21 Record DTOs (`com.college.certifolio.dto`)

By leveraging Java 21 `record` types, DTOs are immutable, compact, and completely decouple JPA entities from JSON serialization:

- **[`AuthRequest.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/dto/AuthRequest.java)**: `record AuthRequest(@NotBlank String email, @NotBlank String password)`
- **[`AuthResponse.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/dto/AuthResponse.java)**: Encapsulates JWT string, token type, userId, email, role, and student demographics.
- **[`CertificateResponse.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/dto/CertificateResponse.java)**: Comprehensive projection of certificate data including file URL, review remarks, and audit timestamps.
- **[`PublicPortfolioResponse.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/dto/PublicPortfolioResponse.java)**: Public projection containing only verified skills and certificates.
- **[`StudentSearchResponse.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/dto/StudentSearchResponse.java)**: Lightweight candidate card for faculty placement filtering.

---

### 2.4 Repositories & Custom JPQL (`com.college.certifolio.repository`)

#### Talent Search JPQL Query in [`StudentProfileRepository.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/repository/StudentProfileRepository.java)
The faculty search console executes a flexible multi-parameter JPQL query:
```java
@Query("""
    SELECT DISTINCT sp FROM StudentProfile sp
    LEFT JOIN sp.studentSkills ss
    LEFT JOIN ss.skill s
    WHERE (:dept IS NULL OR :dept = '' OR LOWER(sp.department) = LOWER(:dept))
      AND (:minCgpa IS NULL OR sp.cgpa >= :minCgpa)
      AND (:skill IS NULL OR :skill = '' OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :skill, '%')))
    ORDER BY sp.cgpa DESC
""")
List<StudentProfile> searchStudents(@Param("skill") String skill, @Param("dept") String dept, @Param("minCgpa") Double minCgpa);
```

#### SQL-Level Isolation in [`CertificationRepository.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/repository/CertificationRepository.java)
```java
List<Certification> findByStudentProfileRollNumberAndStatusOrderByIssueDateDesc(
    String rollNumber, VerificationStatus status
);
```
Guarantees unverified certificates are filtered out at the SQL database query level.

---

### 2.5 Business Services (`com.college.certifolio.service`)

1. **[`FileStorageService.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/service/FileStorageService.java)**:
   - Validates MIME types against `application/pdf`, `image/png`, `image/jpeg`.
   - Enforces a 5 MB maximum file size limit.
   - Generates collision-safe filenames using `UUID.randomUUID() + "_" + rollNo + extension`.
   - Stores files securely under `uploads/certificates/` via `java.nio.file.Files`.

2. **[`PdfExportService.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/service/PdfExportService.java)**:
   - Uses OpenPDF (`com.lowagie.text.*`) to draw an official verified student portfolio.
   - Embeds institutional banners, student demographics, endorsed skill tables, verified certificate grids, and an authenticity guarantee footer with server-generated audit timestamps.

3. **[`DataInitializer.java`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/service/DataInitializer.java)**:
   - Implements Spring `CommandLineRunner`.
   - Automatically pre-seeds 20 standard catalog skills.
   - Creates default faculty account (`faculty@college.edu` / `faculty123`).
   - Creates sample student accounts (`23CS042`, `23CS015`, `23IT028`) with pre-generated PDF proofs.

---

## 3. Frontend Architecture Deep Dive

### 3.1 Network Layer (`src/api/axiosConfig.js`)
Configures a centralized Axios instance:
- **Base URL**: `http://localhost:8080`.
- **Request Interceptor**: Reads `certifolio_token` from `localStorage` and injects `Authorization: Bearer <token>`.
- **Response Interceptor**: Catches HTTP `401 Unauthorized` responses and redirects expired sessions to `/login`.

---

### 3.2 State Management (`src/context/AuthContext.jsx`)
- Uses React Context API to manage global session state.
- Exposes:
  - `user`: Authenticated user metadata (`email`, `role`, `studentProfileId`, `rollNumber`, `fullName`).
  - `login(email, password)`: Submits credentials, updates localStorage, sets state.
  - `register(payload)`: Registers new student/faculty and auto-logs them in.
  - `logout()`: Clears tokens and user state.
  - Role helper booleans: `isStudent` and `isFaculty`.

---

### 3.3 Core Components (`src/components/`)

1. **[`Navbar.jsx`](file:///e:/CertiFolio/frontend/src/components/Navbar.jsx)**:
   - Renders brand identity and responsive navigation links based on user role.
   - Highlights active route with semantic blue badges.
   - Displays user details and one-click Sign Out button.

2. **[`SkillBadge.jsx`](file:///e:/CertiFolio/frontend/src/components/SkillBadge.jsx)**:
   - Renders proficiency pills with specific Tailwind styling:
     - `ADVANCED`: Emerald badge (`bg-emerald-50 text-emerald-700 border-emerald-200`)
     - `INTERMEDIATE`: Blue badge (`bg-blue-50 text-blue-700 border-blue-200`)
     - `BEGINNER`: Slate badge (`bg-slate-100 text-slate-700 border-slate-200`)
   - Includes GitHub/project proof link button and delete trigger.

3. **[`CertUploadModal.jsx`](file:///e:/CertiFolio/frontend/src/components/CertUploadModal.jsx)**:
   - Implements HTML5 Drag-and-Drop file upload zone.
   - Performs client-side MIME check (`.pdf`, `.png`, `.jpg`, `.jpeg`) and size limit check ($\le 5\text{MB}$).
   - Constructs `FormData` and posts to `/api/students/{id}/certifications`.

4. **[`PdfPreviewModal.jsx`](file:///e:/CertiFolio/frontend/src/components/PdfPreviewModal.jsx)**:
   - Renders embedded `<iframe>` for PDF certificate proofs and responsive `<img>` for image uploads.
   - Provides an "Open in New Tab" link for direct inspection.

---

### 3.4 Page Views (`src/pages/`)

1. **[`Login.jsx`](file:///e:/CertiFolio/frontend/src/pages/Login.jsx)**:
   - Authentication form with instant demo buttons ("Student Demo" & "Faculty Demo") for viva demonstrations.
2. **[`Register.jsx`](file:///e:/CertiFolio/frontend/src/pages/Register.jsx)**:
   - Dual-tab registration interface allowing creation of either Student or Faculty accounts with academic profile details.
3. **[`StudentDashboard.jsx`](file:///e:/CertiFolio/frontend/src/pages/StudentDashboard.jsx)**:
   - 3-card academic summary metrics (CGPA, Verified Certifications, Skills Count).
   - Interactive Skill Selector with catalog dropdown and proficiency pills.
   - Active skill matrix grid.
4. **[`StudentCertificates.jsx`](file:///e:/CertiFolio/frontend/src/pages/StudentCertificates.jsx)**:
   - Complete submissions table styled with semantic status pills:
     - Amber for `PENDING AUDIT`
     - Emerald for `VERIFIED`
     - Rose for `REJECTED`
   - In-modal proof inspection trigger.
5. **[`FacultyReview.jsx`](file:///e:/CertiFolio/frontend/src/pages/FacultyReview.jsx)**:
   - Split-view dashboard:
     - Tab 1: **Pending Audits Queue** with one-click Approve / Reject modal and feedback notes.
     - Tab 2: **Talent Search Pool** with multi-parameter filter (Skill, Department, Min CGPA).
6. **[`PublicPortfolio.jsx`](file:///e:/CertiFolio/frontend/src/pages/PublicPortfolio.jsx)**:
   - Public view at `/portfolio/:rollNo`.
   - Displays candidate bio, verified proficiencies, and faculty-audited credentials.
   - Prominent **"Download Official Portfolio PDF"** button triggering OpenPDF download.

---

## 4. Automated Testing Suite (`CertiFolioServiceTests.java`)

Located in [`backend/src/test/java/com/college/certifolio/CertiFolioServiceTests.java`](file:///e:/CertiFolio/backend/src/test/java/com/college/certifolio/CertiFolioServiceTests.java), the test suite executes in Spring Boot test context:

1. **`testVerifyCertificate_transitionsStateAndSetsAuditTimestamp`**:
   - Asserts that a `PENDING` certificate transitions to `VERIFIED` upon faculty review.
   - Confirms faculty remarks are saved and server-side `verifiedAt` timestamp is not null.
2. **`testPublicPortfolio_strictlyFiltersOnlyVerifiedCertificates`**:
   - Creates a `REJECTED` certificate for student `23CS042`.
   - Fetches public portfolio and confirms that only `VERIFIED` certificates are returned, verifying that rejected and pending certificates are excluded.
3. **`testFileStorage_validatesMimeTypesAndRejectsInvalid`**:
   - Tests file upload with valid `application/pdf` and invalid `application/x-msdownload` (`.exe`).
   - Confirms `BadRequestException` is thrown for unauthorized file types.
4. **`testSearchStudents_filtersBySkillDepartmentAndCgpa`**:
   - Queries students matching skill `React`, department `CSE`, and min CGPA `8.5`.
   - Asserts results meet all 3 criteria.
