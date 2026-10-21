# 🎓 CertiFolio

> **Student Skill & Certification Portfolio Registry Application**  
> *A unified, verified credential ledger and talent intelligence showcase built with Java Spring Boot 3.*

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI%203-green.svg)](http://localhost:5173/swagger-ui.html)

---

## 🌟 Overview

**CertiFolio** bridges the gap between students, faculty coordinators, academic institutions, and campus recruiters. It provides:
1. **Student Portfolio Showcase**: High-impact digital portfolios featuring verified technical skills, CGPA, project headlines, and industry credentials.
2. **Certification Verification State Machine**: Faculty verifiers audit student-submitted credentials (`PENDING` $\rightarrow$ `VERIFIED` or `REJECTED`) with an institutional verification stamp.
3. **Skill Matrix & Talent Search**: Recruiter-ready directory to filter students by skill combinations, proficiency levels, and department.
4. **Talent Analytics**: Interactive visual charts (powered by Chart.js) highlighting institutional skill trends and top certification issuers.
5. **REST API & Swagger UI**: Full RESTful interface documented interactively via OpenAPI 3 / Swagger.

---

## 📚 Documentation Suite

For detailed documentation, refer to the guides in the [`docs/`](docs/) directory:

- 📡 **[REST API Documentation](docs/API_DOCUMENTATION.md)**: Full REST endpoint catalog, request/response JSON schemas, query parameters, curl examples, and HTTP error codes.
- 🏗️ **[System Design Documentation](docs/DESIGN_DOCUMENTATION.md)**: Layered architecture, Mermaid sequence and ER diagrams, certification verification state machine, data flows, and scalability considerations.
- 💻 **[Code-Level Documentation](docs/CODE_LEVEL_DOCUMENTATION.md)**: Package breakdown, entity-by-entity, service-by-service, and method-by-method developer documentation, transactional rules, and extension guide.

---

## 🏛️ Architecture

```
                      +-----------------------------+
                      |   Users (Students/Faculty)  |
                      +--------------+--------------+
                                     |
               +---------------------+---------------------+
               |                                           |
               v                                           v
    +----------------------+                    +----------------------+
    |  Web UI (Thymeleaf)  |                    |  REST API & Swagger  |
    |  Bootstrap 5, Chart  |                    |      (/api/v1/*)     |
    +----------+-----------+                    +----------+-----------+
               |                                           |
               +---------------------+---------------------+
                                     |
                                     v
                 +---------------------------------------+
                 |       Service & Business Layer        |
                 | StudentService | CertificationService |
                 |  SkillService  |   AnalyticsService   |
                 +-------------------+-------------------+
                                     |
                                     v
                 +---------------------------------------+
                 |       Data Access Layer (JPA)         |
                 |  StudentRepo   |  CertificationRepo   |
                 |  SkillRepo                            |
                 +-------------------+-------------------+
                                     |
                                     v
                 +---------------------------------------+
                 |       Database (H2 Embedded)          |
                 |  (Switchable to PostgreSQL / MySQL)   |
                 +---------------------------------------+
```

---

## 🚀 Key Features

- **👨‍🎓 Student Profiles & Portfolios**:
  - Full academic details: Registration Number, Department, Year, CGPA, Bio, GitHub, and LinkedIn links.
  - Dedicated public portfolio URL (`/students/{id}/portfolio`) with print/PDF export readiness.
- **📜 Certification Registry**:
  - Track titles, issuers (AWS, Google Cloud, Oracle, Coursera, Meta), issue dates, and expiry dates.
  - Public proof link validation and credential IDs.
- **🛡️ Faculty Audit Portal (`/audit`)**:
  - Institutional review queue for submitted certifications.
  - One-click approval and stamping with verifier name and audit notes.
  - Rejection workflow with constructive feedback.
- **🧠 Institutional Skill Matrix (`/skills`)**:
  - Categorized into *Programming Languages*, *Frameworks*, *Cloud & DevOps*, *AI/Data*, *Cybersecurity*, and *Soft Skills*.
  - 1–5 star ratings and proficiency indicators (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`).
- **📊 Interactive Analytics Dashboard (`/analytics`)**:
  - Top technical skills bar chart.
  - Certification issuer market share chart.
  - Verification pass rates and metrics.
- **📑 OpenAPI 3 & Swagger UI (`/swagger-ui.html`)**:
  - Interactive exploration and live testing of all JSON REST endpoints.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Backend Framework** | Spring Boot 3.3.5, Spring Web MVC, Spring Data JPA |
| **Language & Platform** | Java 21+ / OpenJDK |
| **Templating & UI** | Thymeleaf, Bootstrap 5.3, FontAwesome 6, Chart.js 4.4 |
| **Database** | H2 Database (In-Memory / File-based) with H2 Web Console |
| **API Documentation** | Springdoc OpenAPI 3.0 / Swagger UI |
| **Build & Tooling** | Apache Maven 3.9+ with bundled Maven Wrapper (`mvnw`) |

---

## 🏁 Getting Started

### Prerequisites
- Java 17, 21, or 24 installed
- Git

### 1. Clone the repository
```bash
git clone <repository-url>
cd CertiFolio1
```

### 2. Build and run the application

#### Option A: One-Click Runner (Windows)
Simply double-click:
```bat
run.bat
```
*(Automatically detects Java, starts the Spring Boot server on port 5173, and opens your browser.)*

Or via PowerShell:
```powershell
.\run.ps1
```

#### Option B: Maven Command
```bash
# On Windows PowerShell / CMD:
.\mvnw spring-boot:run

# On Linux / macOS:
./mvnw spring-boot:run
```

The application will start on `http://localhost:5173`.

### 3. Explore Key Pages
- 🌐 **Home Dashboard**: [http://localhost:5173/](http://localhost:5173/)
- 👥 **Student Directory**: [http://localhost:5173/students](http://localhost:5173/students)
- 🧠 **Skill Matrix**: [http://localhost:5173/skills](http://localhost:5173/skills)
- 📜 **Certifications**: [http://localhost:5173/certifications](http://localhost:5173/certifications)
- 🛡️ **Verifier Audit Console**: [http://localhost:5173/audit](http://localhost:5173/audit)
- 📈 **Talent Analytics**: [http://localhost:5173/analytics](http://localhost:5173/analytics)
- 📖 **Swagger UI (REST API)**: [http://localhost:5173/swagger-ui.html](http://localhost:5173/swagger-ui.html)
- 🗄️ **H2 Database Console**: [http://localhost:5173/h2-console](http://localhost:5173/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:certifoliodb`, User: `sa`, Password: empty)*

---

## 🧪 Running Automated Tests

Run unit and integration tests with:
```bash
.\mvnw test
```

---

## 🔌 REST API Endpoints Overview

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/students` | List all students or filter by query, department, or skill |
| `GET` | `/api/v1/students/{id}` | Get student profile details |
| `POST` | `/api/v1/students` | Register a new student profile |
| `PUT` | `/api/v1/students/{id}` | Update existing student profile |
| `DELETE` | `/api/v1/students/{id}` | Delete student profile |
| `GET` | `/api/v1/skills` | List skills or filter by category/keyword |
| `POST` | `/api/v1/skills` | Add skill to student profile |
| `GET` | `/api/v1/certifications` | List certifications or filter by verification status |
| `GET` | `/api/v1/certifications/pending`| Get all pending certifications awaiting review |
| `POST` | `/api/v1/certifications` | Submit a certification |
| `PATCH`| `/api/v1/certifications/{id}/verify` | Approve or reject a certification (with notes) |
| `GET` | `/api/v1/analytics/summary` | Get aggregated registry statistics |

---

## 📄 License
This project is open-source and licensed under the [Apache 2.0 License](LICENSE).
