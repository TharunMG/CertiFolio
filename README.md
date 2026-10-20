# CertiFolio: Student Skill & Certification Registry

> **Verified Academic Skill & Credential Registry**  
> *B.Tech / B.E. Computer Science Mini-Project Blueprint Implementation*  
> Built with **React.js 19 + Tailwind CSS + Java 21 / 24 + Spring Boot 3.3 + OpenPDF + H2/MySQL**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%20%7C%2024%20LTS-orange.svg)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-18%20%2F%2019-blue.svg)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38bdf8.svg)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 1. Project Overview

**CertiFolio** solves the academic challenge of unverified student claims by providing an institutional credential and skill registry. Students upload digital course certificates (PDF/PNG/JPEG) from platforms like NPTEL, Coursera, or AWS, and attach technical skill tags with proficiency levels and project repository links. 

Department faculty members audit submissions inside an interactive previewer, approve or reject them with audit remarks, and search placement-ready talent by department, CGPA, and specific technical competencies. Public shareable portfolios strictly filter only **`VERIFIED`** credentials to protect institutional integrity.

---

## 2. Full-Stack System Architecture

```
┌─────────────────────────────────┐                 ┌──────────────────────────────────────┐
│       React.js Frontend         │                 │       Spring Boot REST API           │
│   (Vite, Tailwind, Axios JWT)   │  HTTP / JSON /  │  (SecurityFilterChain, DTO Records)  │
│          Port: 5173             │◄───────────────►│             Port: 8080               │
└─────────────────────────────────┘    Multipart    └──────────────────┬───────────────────┘
                                                                       │
                                            ┌──────────────────────────┴─────────────────────────┐
                                            ▼                                                    ▼
                             ┌───────────────────────────────┐                    ┌───────────────────────────────┐
                             │    H2 Database / MySQL 8.0    │                    │     NIO File Storage          │
                             │      5 Normalized Tables      │                    │   /uploads/certificates/      │
                             │  Spring Data JPA Repositories │                    │    UUID Collision Prevention  │
                             └───────────────────────────────┘                    └───────────────────────────────┘
```

---

## 3. Relational Database Schema & JPA Entities

| Entity | Table | Key Columns & Constraints | JPA Relationship Mapping |
| :--- | :--- | :--- | :--- |
| **User** | `users` | `id` (PK), `email` (Unique, Not Null), `password_hash`, `role` (`ROLE_STUDENT`, `ROLE_FACULTY`), `created_at` | `@OneToOne(mappedBy = "user")` with `StudentProfile` |
| **StudentProfile** | `student_profiles` | `id` (PK), `roll_number` (Unique), `full_name`, `department`, `year_of_study`, `cgpa`, `github_url`, `linkedin_url` | `@OneToOne` with `User`, `@OneToMany` with `StudentSkill` & `Certification` |
| **Skill** | `skills` | `id` (PK), `skill_name` (Unique), `category` (`BACKEND`, `FRONTEND`, `DATABASE`, `CLOUD`, `TOOLS`) | Master catalog referenced by `StudentSkill` |
| **StudentSkill** | `student_skills` | `id` (PK), `proficiency` (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`), `project_link`, `endorsed` | `@ManyToOne` joining `StudentProfile` and `Skill` |
| **Certification** | `certifications` | `id` (PK), `title`, `issuer`, `credential_id`, `issue_date`, `file_path`, `status` (`PENDING`, `VERIFIED`, `REJECTED`), `remarks`, `verified_at` | `@ManyToOne(fetch = LAZY)` joining `StudentProfile` |

---

## 4. REST API Specification

| Method | Endpoint | Access Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and issues stateless JWT Bearer token |
| `POST` | `/api/auth/register` | Public | Registers a new student or faculty account |
| `GET` | `/api/portfolio/{rollNo}` | Public | Fetches student bio, skills, and strictly `VERIFIED` certificates |
| `GET` | `/api/portfolio/{rollNo}/pdf` | Public | Generates downloadable official OpenPDF transcript |
| `GET` | `/api/skills` | Public | Lists all master skills in catalog |
| `GET` | `/api/files/{fileName}` | Public | Serves certificate proofs for in-browser modal preview |
| `GET` | `/api/students/{id}/profile` | Student / Faculty | Fetches student profile metrics |
| `POST` | `/api/students/{id}/skills` | Student | Links a master skill and proficiency pill to student profile |
| `DELETE` | `/api/students/{id}/skills/{skillId}` | Student | Removes skill from student profile |
| `POST` | `/api/students/{id}/certifications` | Student | Uploads multipart PDF/PNG file and creates `PENDING` record |
| `GET` | `/api/students/{id}/certifications` | Student / Faculty | Retrieves all certificate submissions and verification status |
| `GET` | `/api/faculty/certifications/pending` | Faculty | Populates faculty audit review queue |
| `PATCH` | `/api/faculty/certifications/{id}/verify`| Faculty | Updates status to `VERIFIED` or `REJECTED` with remarks |
| `GET` | `/api/faculty/students/search` | Faculty | Multi-filter talent query (`?skill=...&dept=...&minCgpa=...`) |

---

## 5. Live Viva Demonstration Credentials

The application is pre-seeded via `CommandLineRunner` with ready-to-test accounts and proof files:

| Account Type | Email | Password | Role / Details |
| :--- | :--- | :--- | :--- |
| **Student** | `student@college.edu` | `student123` | Arun Kumar (`23CS042`), CSE, CGPA: 8.8 (Has verified & pending proofs) |
| **Student** | `priya@college.edu` | `student123` | Priya Sharma (`23CS015`), CSE, CGPA: 9.3 |
| **Student** | `rahul@college.edu` | `student123` | Rahul Verma (`23IT028`), IT, CGPA: 7.9 |
| **Faculty** | `faculty@college.edu` | `faculty123` | Faculty Auditor & Placement Coordinator |

> [!TIP]
> **One-Click Quick Logins**: The login page includes quick-fill buttons for both Student and Faculty to accelerate your viva presentation.

---

## 6. How to Run the Project

### Prerequisites
- Java 21 or higher (Java 24 fully supported)
- Node.js (v18+) & npm

### Step 1: Start the Spring Boot Backend
Open a terminal in `backend/`:
```bash
# In Windows PowerShell:
$env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
mvn clean spring-boot:run
```
- API Base URL: `http://localhost:8080`
- Swagger UI (Interactive API Docs): `http://localhost:8080/swagger-ui/index.html`
- H2 Database Web Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/certifoliodb`, User: `sa`, Password: empty)

### Step 2: Start the React Frontend
Open a separate terminal in `frontend/`:
```bash
npm install
npm run dev
```
- Frontend SPA: `http://localhost:5173`

---

## 7. Viva-Voce Standout Tips

During evaluations and viva questioning, be sure to highlight the **two architectural safeguards** against forged credentials:

1. **SQL-Level Filtering**: Public portfolios execute a parameterized JPQL query strictly filtered by `status = 'VERIFIED'`. Unverified or rejected submissions are never transmitted to unauthorized clients.
2. **Audit Timestamp & Remarks**: Every verified credential captures the faculty auditor's immutable review timestamp (`verified_at`) and digital remarks.
3. **OpenPDF Integration**: The `/api/portfolio/{rollNo}/pdf` endpoint utilizes OpenPDF to draw a clean transcript containing verified skills, credentials, and institutional guarantees on-the-fly.

---

## 8. Automated Testing

Run the automated JUnit 5 test suite:
```bash
mvn test
```
**Test Coverage Includes**:
- Verification audit state machine transitions (`PENDING` $\to$ `VERIFIED`)
- Public portfolio SQL status filtering
- File storage MIME type & size validation
- Multi-parameter JPQL talent search query
