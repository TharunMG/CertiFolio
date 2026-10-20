# CertiFolio: REST API Documentation

This document provides complete, formal documentation for the **CertiFolio** RESTful Web API.

---

## 1. API Architecture & Standards

- **Base URL**: `http://localhost:8080`
- **Protocol**: HTTP/1.1 with JSON payloads and `multipart/form-data` for file uploads.
- **Stateless Authentication**: JSON Web Tokens (JWT) using HMAC-SHA256.
- **Authorization Header**:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```
- **Interactive Documentation**: Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.
- **OpenAPI 3.0 Specification**: Available in JSON at `http://localhost:8080/api-docs`.

---

## 2. Standard Error Response Schema

All errors handled by [`GlobalExceptionHandler`](file:///e:/CertiFolio/backend/src/main/java/com/college/certifolio/exception/GlobalExceptionHandler.java) follow a uniform JSON structure:

```json
{
  "timestamp": "2026-10-20T21:03:40.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid verification status: UNKNOWN. Allowed: VERIFIED, REJECTED"
}
```

For validation errors (`400 Bad Request`), field-specific diagnostics are included:

```json
{
  "timestamp": "2026-10-20T21:03:40.123",
  "status": 400,
  "error": "Validation Failed",
  "validationErrors": {
    "email": "Email should be valid",
    "password": "Password is required"
  }
}
```

---

## 3. Authentication Endpoints (`/api/auth`)

### 3.1 Authenticate User (Login)

Validates user credentials and issues a stateless Bearer JWT token.

- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "email": "student@college.edu",
  "password": "student123"
}
```

#### Response: `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 2,
  "email": "student@college.edu",
  "role": "ROLE_STUDENT",
  "studentProfileId": 1,
  "rollNumber": "23CS042",
  "fullName": "Arun Kumar"
}
```

#### Error Responses:
- `400 Bad Request`: Invalid email or password, or validation failed.

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@college.edu","password":"student123"}'
```

---

### 3.2 Register New Account

Creates a new institutional user. If `role` is `STUDENT`, creates the linked `StudentProfile` record.

- **URL**: `/api/auth/register`
- **Method**: `POST`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body (Student Example)
```json
{
  "email": "priya@college.edu",
  "password": "student123",
  "role": "STUDENT",
  "fullName": "Priya Sharma",
  "rollNumber": "23CS015",
  "department": "CSE",
  "yearOfStudy": 2,
  "cgpa": 9.3,
  "githubUrl": "https://github.com/priyasharma",
  "linkedinUrl": "https://linkedin.com/in/priyasharma"
}
```

#### Request Body (Faculty Example)
```json
{
  "email": "faculty@college.edu",
  "password": "faculty123",
  "role": "FACULTY",
  "fullName": "Dr. Ramesh Babu"
}
```

#### Response: `200 OK`
Returns the generated `AuthResponse` with JWT token.

---

## 4. Student Endpoints (`/api/students`)

Requires header: `Authorization: Bearer <TOKEN>` with role `ROLE_STUDENT` or `ROLE_ADMIN`.

### 4.1 Get Student Profile
- **URL**: `/api/students/{id}/profile`
- **Method**: `GET`
- **Access**: `ROLE_STUDENT`, `ROLE_FACULTY`, `ROLE_ADMIN`

#### Response: `200 OK`
```json
{
  "id": 1,
  "rollNumber": "23CS042",
  "fullName": "Arun Kumar",
  "department": "CSE",
  "yearOfStudy": 2,
  "cgpa": 8.8,
  "githubUrl": "https://github.com/arunkumar",
  "linkedinUrl": "https://linkedin.com/in/arunkumar"
}
```

---

### 4.2 Get Student Attached Skills
- **URL**: `/api/students/{id}/skills`
- **Method**: `GET`
- **Access**: `ROLE_STUDENT`, `ROLE_FACULTY`

#### Response: `200 OK`
```json
[
  {
    "id": 1,
    "skillId": 1,
    "skillName": "Java",
    "category": "BACKEND",
    "proficiency": "ADVANCED",
    "projectLink": "https://github.com/arunkumar/java-algorithms",
    "endorsed": true
  },
  {
    "id": 2,
    "skillId": 2,
    "skillName": "Spring Boot",
    "category": "BACKEND",
    "proficiency": "INTERMEDIATE",
    "projectLink": "https://github.com/arunkumar/certifolio-api",
    "endorsed": true
  }
]
```

---

### 4.3 Attach Skill to Profile
- **URL**: `/api/students/{id}/skills`
- **Method**: `POST`
- **Access**: `ROLE_STUDENT`
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "skillId": 6,
  "proficiency": "INTERMEDIATE",
  "projectLink": "https://github.com/arunkumar/react-dashboard"
}
```

#### Response: `201 Created`
Returns the created `StudentSkillDto`.

---

### 4.4 Remove Skill from Profile
- **URL**: `/api/students/{id}/skills/{skillId}`
- **Method**: `DELETE`
- **Access**: `ROLE_STUDENT`

#### Response: `200 OK`
```json
{
  "message": "Skill removed successfully"
}
```

---

### 4.5 Upload Certificate Proof
Submits a digital course credential and binary proof file (PDF, PNG, JPEG) up to 5 MB. Automatically sets status to `PENDING`.

- **URL**: `/api/students/{id}/certifications`
- **Method**: `POST`
- **Access**: `ROLE_STUDENT`
- **Content-Type**: `multipart/form-data`

#### Form Parameters:
| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `file` | Binary | Yes | Certificate proof (PDF/PNG/JPEG, max 5 MB) |
| `title` | String | Yes | Course/Certificate title |
| `issuer` | String | Yes | Issuing organization (e.g., NPTEL, AWS, Coursera) |
| `credentialId`| String | No | Credential verification ID / license key |
| `issueDate` | Date | No | Format: `YYYY-MM-DD` |

#### Response: `201 Created`
```json
{
  "id": 3,
  "studentProfileId": 1,
  "studentRollNumber": "23CS042",
  "studentName": "Arun Kumar",
  "studentDepartment": "CSE",
  "title": "AWS Certified Cloud Practitioner",
  "issuer": "AWS",
  "credentialId": "AWS-CCP-98124",
  "issueDate": "2025-08-20",
  "filePath": "c8a14b09-1234_23CS042.pdf",
  "fileUrl": "/api/files/c8a14b09-1234_23CS042.pdf",
  "status": "PENDING",
  "remarks": null,
  "verifiedAt": null,
  "createdAt": "2026-10-20T21:05:00.000"
}
```

---

### 4.6 Get All Student Certificates
- **URL**: `/api/students/{id}/certifications`
- **Method**: `GET`
- **Access**: `ROLE_STUDENT`, `ROLE_FACULTY`

#### Response: `200 OK`
Returns a list of all certificate records submitted by the student, along with status (`PENDING`, `VERIFIED`, `REJECTED`) and reviewer remarks.

---

## 5. Faculty & Audit Endpoints (`/api/faculty`)

Requires header: `Authorization: Bearer <TOKEN>` with role `ROLE_FACULTY` or `ROLE_ADMIN`.

### 5.1 Get Pending Verifications Queue
- **URL**: `/api/faculty/certifications/pending`
- **Method**: `GET`
- **Access**: `ROLE_FACULTY`

#### Response: `200 OK`
Returns an array of unverified certificate submissions sorted by submission date descending.

---

### 5.2 Audit & Verify Certificate
Transitions a certificate submission from `PENDING` to `VERIFIED` or `REJECTED`, records faculty remarks, and saves the immutable `verifiedAt` audit timestamp.

- **URL**: `/api/faculty/certifications/{id}/verify`
- **Method**: `PATCH`
- **Access**: `ROLE_FACULTY`
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "status": "VERIFIED",
  "remarks": "Score: 92% Elite+Gold. Verified against NPTEL central roll registry."
}
```

#### Response: `200 OK`
```json
{
  "id": 1,
  "studentProfileId": 1,
  "studentRollNumber": "23CS042",
  "studentName": "Arun Kumar",
  "studentDepartment": "CSE",
  "title": "Programming in Java (Elite+Gold)",
  "issuer": "NPTEL",
  "credentialId": "NPTEL23CS4289",
  "issueDate": "2025-04-15",
  "filePath": "sample_certificate_23CS042.pdf",
  "fileUrl": "/api/files/sample_certificate_23CS042.pdf",
  "status": "VERIFIED",
  "remarks": "Score: 92% Elite+Gold. Verified against NPTEL central roll registry.",
  "verifiedAt": "2026-10-20T21:03:46.000",
  "createdAt": "2026-10-20T20:55:00.000"
}
```

---

### 5.3 Filter Placement Talent Pool
Enables department coordinators and placement officers to query students by technical skill name, department, and minimum CGPA.

- **URL**: `/api/faculty/students/search`
- **Method**: `GET`
- **Access**: `ROLE_FACULTY`

#### Query Parameters:
| Param | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `skill` | String | No | Filter by tech skill (e.g. `React`, `Java`) |
| `dept` | String | No | Filter by department (e.g. `CSE`, `IT`) |
| `minCgpa`| Number | No | Filter by minimum CGPA (e.g. `8.0`) |

#### Example Request
`GET /api/faculty/students/search?skill=React&dept=CSE&minCgpa=8.0`

#### Response: `200 OK`
```json
[
  {
    "id": 1,
    "rollNumber": "23CS042",
    "fullName": "Arun Kumar",
    "department": "CSE",
    "yearOfStudy": 2,
    "cgpa": 8.8,
    "githubUrl": "https://github.com/arunkumar",
    "linkedinUrl": "https://linkedin.com/in/arunkumar",
    "skills": ["Java", "Spring Boot", "React", "MySQL"],
    "verifiedCertCount": 2
  }
]
```

---

## 6. Public & Portfolio Endpoints (`/api/portfolio`)

Publicly accessible endpoints requiring no authentication.

### 6.1 Get Public Verified Portfolio
Architectural safeguard: strictly executes a database query filtered by `status = 'VERIFIED'`. Unverified or rejected proofs are never returned.

- **URL**: `/api/portfolio/{rollNo}`
- **Method**: `GET`
- **Access**: Public

#### Response: `200 OK`
```json
{
  "rollNumber": "23CS042",
  "fullName": "Arun Kumar",
  "department": "CSE",
  "yearOfStudy": 2,
  "cgpa": 8.8,
  "githubUrl": "https://github.com/arunkumar",
  "linkedinUrl": "https://linkedin.com/in/arunkumar",
  "skills": [
    {
      "id": 1,
      "skillId": 1,
      "skillName": "Java",
      "category": "BACKEND",
      "proficiency": "ADVANCED",
      "projectLink": "https://github.com/arunkumar/java-algorithms",
      "endorsed": true
    }
  ],
  "verifiedCertificates": [
    {
      "id": 1,
      "studentProfileId": 1,
      "studentRollNumber": "23CS042",
      "studentName": "Arun Kumar",
      "studentDepartment": "CSE",
      "title": "Programming in Java (Elite+Gold)",
      "issuer": "NPTEL",
      "credentialId": "NPTEL23CS4289",
      "issueDate": "2025-04-15",
      "filePath": "sample_certificate_23CS042.pdf",
      "fileUrl": "/api/files/sample_certificate_23CS042.pdf",
      "status": "VERIFIED",
      "remarks": "Score: 92% Elite+Gold. Verified against NPTEL central roll registry.",
      "verifiedAt": "2026-10-10T14:30:00",
      "createdAt": "2026-10-10T12:00:00"
    }
  ]
}
```

---

### 6.2 Download Official Portfolio PDF (OpenPDF)
Dynamically compiles and streams a PDF transcript with student bio, endorsed skills, verified certifications, and institutional authenticity timestamp.

- **URL**: `/api/portfolio/{rollNo}/pdf`
- **Method**: `GET`
- **Access**: Public
- **Response Headers**:
  - `Content-Type: application/pdf`
  - `Content-Disposition: inline; filename="{rollNo}_Portfolio.pdf"`
- **Response**: Binary PDF byte stream.

---

## 7. Master Catalog & File Serving Endpoints

### 7.1 List Master Skill Catalog
- **URL**: `/api/skills`
- **Method**: `GET`
- **Access**: Public

#### Response: `200 OK`
Returns all 20 pre-seeded skills categorized by Backend, Frontend, Database, Cloud, and Tools.

---

### 7.2 Inspect Certificate Proof File
- **URL**: `/api/files/{fileName}`
- **Method**: `GET`
- **Access**: Public
- **Response**: Binary file stream with MIME detection (`application/pdf`, `image/png`, `image/jpeg`).
