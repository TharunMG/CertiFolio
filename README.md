# CertiFolio: Student Skill & Certification Registry

> **Verified Academic Skill & Credential Registry**  
> *B.Tech / B.E. 2nd-Year Computer Science Mini-Project Blueprint Implementation*  
> Built with **React.js 19 + Tailwind CSS + Java 21 / 24 + Spring Boot 3.3 + OpenPDF + H2/MySQL**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%20%7C%2024%20LTS-orange.svg)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-18%20%2F%2019-blue.svg)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38bdf8.svg)](https://tailwindcss.com/)
[![OpenPDF](https://img.shields.io/badge/OpenPDF-2.0.3-red.svg)](https://github.com/LibrePDF/OpenPDF)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-green.svg)](http://localhost:8080/swagger-ui/index.html)

---

## 📑 Evaluation Deliverables & Documentation Index

In accordance with mini-project review criteria, the documentation is organized into four formal deliverables:

| Deliverable | Document Link | Description |
| :--- | :--- | :--- |
| **1. Project Overview & Quickstart** | [**README.md**](README.md) *(This Document)* | Project identity, branding, features, running instructions, and demo credentials |
| **2. API Documentation** | [**docs/API_DOCUMENTATION.md**](docs/API_DOCUMENTATION.md) | Exhaustive REST API contract, request/response JSON schemas, cURL examples, and error handling |
| **3. Design Documentation** | [**docs/DESIGN_DOCUMENTATION.md**](docs/DESIGN_DOCUMENTATION.md) | System topology, Mermaid architecture, ER diagram, UML class diagram, sequence diagrams, and state machine |
| **4. Code-Level Documentation** | [**docs/CODE_LEVEL_DOCUMENTATION.md**](docs/CODE_LEVEL_DOCUMENTATION.md) | Class-by-class backend breakdown, Java 21 records, React components, state context, and JUnit 5 tests |

---

## 1. Executive Summary & Problem Statement

In academic institutions, students accumulate diverse credentials from external learning platforms (NPTEL, Coursera, AWS, HackerRank) and declare technical proficiencies on their resumes. However, placement departments and evaluators face significant challenges in **verifying the authenticity of these claims**.

**CertiFolio** solves this challenge through a tamper-resistant, faculty-audited registry:
1. **Student Dashboard & Skill Matrix**: Students register competencies with proficiency levels (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), attach GitHub proof projects, and upload digital certificates (PDF/PNG/JPEG).
2. **Faculty Verification Console**: Authorized faculty audit submissions via an in-browser previewer, confirm credential IDs, and approve or reject proofs with immutable audit timestamps and remarks.
3. **Talent Discovery Search**: Placement officers query students by exact skill combinations, department, and minimum CGPA.
4. **Public Verified Portfolio**: A publicly shareable profile strictly filtering only `VERIFIED` credentials, equipped with a one-click **Official OpenPDF Transcript Generator**.

---

## 2. Decoupled System Architecture

```
┌──────────────────────────────────────┐                   ┌────────────────────────────────────────┐
│          React 19 SPA (Vite)         │   JSON / HTTP     │          Spring Boot REST API          │
│  - Tailwind CSS Responsive Design    │◄─────────────────►│  - Stateless JWT SecurityFilterChain  │
│  - Axios with Bearer Interceptor     │    Multipart      │  - Java 21 Record DTOs                 │
│  - In-Browser PDF/Image Previewer    │                   │  - OpenPDF Dynamic PDF Engine          │
│             Port: 5173               │                   │              Port: 8080                │
└──────────────────────────────────────┘                   └───────────────────┬────────────────────┘
                                                                               │
                                                    ┌──────────────────────────┴─────────────────────────┐
                                                    ▼                                                    ▼
                                     ┌───────────────────────────────┐                    ┌───────────────────────────────┐
                                     │     H2 / MySQL 8.0 Database   │                    │     NIO File Storage          │
                                     │   5 Normalized Relational     │                    │   /uploads/certificates/      │
                                     │   Spring Data JPA Repos       │                    │   Collision-Safe UUID Proofs  │
                                     └───────────────────────────────┘                    └───────────────────────────────┘
```

---

## 3. Relational Database Normalization (5 Core Tables)

1. **`users`**: Email (Unique), password hash (BCrypt), role (`ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`), creation timestamp.
2. **`student_profiles`**: Roll number (Unique), full name, department, year of study, CGPA, GitHub URL, LinkedIn URL.
3. **`skills`**: Master catalog of industry skills across Backend, Frontend, Database, Cloud, and Tools.
4. **`student_skills`**: Join table mapping students to skills with proficiency levels (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), project links, and endorsement flags.
5. **`certifications`**: Student certificate records with title, issuer, credential ID, file path, status (`PENDING`, `VERIFIED`, `REJECTED`), faculty remarks, and audit timestamps.

---

## 4. Single-Command Launch (Zero Configuration)

CertiFolio includes one-click launch scripts that automatically detect Java 24 and Maven, start both services in parallel, and open your web browser:

### Option A: Windows Batch File (Double-Click)
Double-click [`run.bat`](run.bat) from File Explorer, or run:
```cmd
.\run.bat
```

### Option B: PowerShell
```powershell
.\run.ps1
```

### Option C: Manual Startup
- **Backend (Port 8080)**:
  ```powershell
  cd backend
  $env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
  mvn spring-boot:run
  ```
- **Frontend (Port 5173)**:
  ```powershell
  cd frontend
  npm run dev
  ```

---

## 5. Live Viva Demonstration Accounts

Pre-seeded via [`DataInitializer`](backend/src/main/java/com/college/certifolio/service/DataInitializer.java) with ready-to-test profiles:

| Account Type | Email | Password | Role / Details |
| :--- | :--- | :--- | :--- |
| **Student** | `student@college.edu` | `student123` | Arun Kumar (`23CS042`), CSE, CGPA: 8.8 (Has verified & pending proofs) |
| **Student** | `priya@college.edu` | `student123` | Priya Sharma (`23CS015`), CSE, CGPA: 9.3 |
| **Student** | `rahul@college.edu` | `student123` | Rahul Verma (`23IT028`), IT, CGPA: 7.9 |
| **Faculty Auditor** | `faculty@college.edu` | `faculty123` | Faculty Reviewer & Placement Coordinator |

> [!TIP]
> **Viva Quick-Fill Buttons**: The Login page includes instant demo buttons ("Student Demo" & "Faculty Demo") so you never have to manually type credentials during viva presentations!

---

## 6. Viva-Voce Standout Questions & Architectural Safeguards

Evaluators frequently ask: *"How does your system prevent students from uploading fake or unverified certificates to their public portfolio?"*

Highlight these two architectural safeguards:
1. **SQL-Level Status Filtering**: The public portfolio endpoint (`GET /api/portfolio/{rollNo}`) executes an explicit JPA query requiring `status = 'VERIFIED'`. Unverified or rejected submissions are never transmitted over the wire to public viewers.
2. **Immutable Audit Timestamps & Remarks**: Every verified credential captures the exact server timestamp (`verified_at`) and signed remarks of the institutional faculty reviewer.
3. **One-Click OpenPDF Transcript**: Evaluators can click "Download Official Portfolio PDF" to immediately inspect an institutional document dynamically drawn with student demographics, endorsed skills, and verified certificates.

---

## 7. Automated Test Suite

Run the 4 automated JUnit 5 tests covering verification logic, security filtering, file validation, and talent search:
```powershell
cd backend
mvn test
```
Result: **Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 (BUILD SUCCESS)**.
