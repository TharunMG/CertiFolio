# 📡 CertiFolio - REST API Documentation

This document provides a comprehensive reference for the **CertiFolio REST API**.

- **Base URL**: `http://localhost:5173/api/v1`
- **Interactive Swagger UI**: [http://localhost:5173/swagger-ui.html](http://localhost:5173/swagger-ui.html)
- **OpenAPI 3 JSON Specification**: [http://localhost:5173/v3/api-docs](http://localhost:5173/v3/api-docs)
- **Default Format**: `application/json`

---

## 📑 Table of Contents
1. [Standard Error Response Format](#standard-error-response-format)
2. [Students API](#1-students-api)
   - [List / Search Students](#get-apiv1students)
   - [Get Student by ID](#get-apiv1studentsid)
   - [Get Student by Registration Number](#get-apiv1studentsregregno)
   - [Register Student](#post-apiv1students)
   - [Update Student](#put-apiv1studentsid)
   - [Delete Student](#delete-apiv1studentsid)
3. [Skills API](#2-skills-api)
   - [List / Filter Skills](#get-apiv1skills)
   - [Get Skill by ID](#get-apiv1skillsid)
   - [Get Skills by Student ID](#get-apiv1skillsstudentstudentid)
   - [Add Skill to Student](#post-apiv1skills)
   - [Update Skill](#put-apiv1skillsid)
   - [Delete Skill](#delete-apiv1skillsid)
4. [Certifications API](#3-certifications-api)
   - [List / Filter Certifications](#get-apiv1certifications)
   - [Get Certification by ID](#get-apiv1certificationsid)
   - [Get Pending Certifications (Audit Queue)](#get-apiv1certificationspending)
   - [Get Certifications by Student](#get-apiv1certificationsstudentstudentid)
   - [Submit Certification](#post-apiv1certifications)
   - [Update Certification](#put-apiv1certificationsid)
   - [Verify / Reject Certification](#patch-apiv1certificationsidverify)
   - [Delete Certification](#delete-apiv1certificationsid)
5. [Analytics API](#4-analytics-api)
   - [Get Registry Analytics Summary](#get-apiv1analyticssummary)

---

## Standard Error Response Format

When an API error occurs (4xx or 5xx), the response returns a structured JSON payload:

```json
{
  "timestamp": "2026-10-21T12:00:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with ID: 99"
}
```

For validation failures (`400 Bad Request`):
```json
{
  "timestamp": "2026-10-21T12:00:00.000",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "email": "Valid email is required",
    "regNo": "Registration number is required"
  }
}
```

---

## 1. Students API

### `GET /api/v1/students`
Retrieves all registered students or searches students based on keyword, department, or skill.

#### Query Parameters:
| Parameter | Type | Required | Description |
|---|---|---|---|
| `search` | String | No | Search query matching name, regNo, email, or department |
| `department` | String | No | Filter by exact department name (e.g. `Computer Science & Engineering`) |
| `skill` | String | No | Filter students possessing a specific skill (e.g. `Java`, `AWS`) |

#### Example Request:
```bash
curl -X GET "http://localhost:5173/api/v1/students?search=Aarav"
```

#### Example Response (`200 OK`):
```json
[
  {
    "id": 1,
    "regNo": "REG2023CS0101",
    "fullName": "Aarav Sharma",
    "email": "aarav.sharma@certifolio.edu",
    "department": "Computer Science & Engineering",
    "yearOfStudy": 4,
    "cgpa": 9.25,
    "bio": "Passionate Cloud & Full-Stack Java Engineer.",
    "githubUrl": "https://github.com/aaravsharma",
    "linkedinUrl": "https://linkedin.com/in/aarav-sharma-dev",
    "createdAt": "2026-10-21T11:45:00",
    "skills": [
      {
        "id": 1,
        "name": "Java",
        "category": "PROGRAMMING_LANGUAGE",
        "proficiencyLevel": "EXPERT",
        "rating": 5
      }
    ],
    "certifications": [
      {
        "id": 1,
        "title": "AWS Certified Solutions Architect - Associate",
        "issuingOrganization": "Amazon Web Services",
        "issueDate": "2025-03-15",
        "expiryDate": "2028-03-15",
        "credentialId": "AWS-SAA-904128",
        "credentialUrl": "https://aws.amazon.com/verification",
        "verificationStatus": "VERIFIED",
        "verifierNotes": "Credential verified via AWS certification directory.",
        "verifiedAt": "2026-09-21T11:45:00",
        "verifiedBy": "Dr. R. K. Gupta (Faculty Coordinator)"
      }
    ]
  }
]
```

---

### `GET /api/v1/students/{id}`
Retrieves a single student by numeric ID.

#### Example Response (`200 OK`):
```json
{
  "id": 1,
  "regNo": "REG2023CS0101",
  "fullName": "Aarav Sharma",
  "email": "aarav.sharma@certifolio.edu",
  "department": "Computer Science & Engineering",
  "yearOfStudy": 4,
  "cgpa": 9.25,
  "bio": "Passionate Cloud & Full-Stack Java Engineer.",
  "githubUrl": "https://github.com/aaravsharma",
  "linkedinUrl": "https://linkedin.com/in/aarav-sharma-dev",
  "skills": [],
  "certifications": []
}
```

---

### `GET /api/v1/students/reg/{regNo}`
Retrieves a student profile by institutional registration number.

#### Example Request:
```bash
curl -X GET "http://localhost:5173/api/v1/students/reg/REG2023CS0101"
```

---

### `POST /api/v1/students`
Registers a new student in the registry.

#### Request Body (`application/json`):
```json
{
  "regNo": "REG2026CS0501",
  "fullName": "Ananya Sen",
  "email": "ananya.sen@certifolio.edu",
  "department": "Computer Science & Engineering",
  "yearOfStudy": 2,
  "cgpa": 8.95,
  "bio": "Competitive programmer and backend enthusiast.",
  "githubUrl": "https://github.com/ananyasen",
  "linkedinUrl": "https://linkedin.com/in/ananyasen"
}
```

#### Example Response (`201 Created`):
```json
{
  "id": 6,
  "regNo": "REG2026CS0501",
  "fullName": "Ananya Sen",
  "email": "ananya.sen@certifolio.edu",
  "department": "Computer Science & Engineering",
  "yearOfStudy": 2,
  "cgpa": 8.95,
  "bio": "Competitive programmer and backend enthusiast.",
  "githubUrl": "https://github.com/ananyasen",
  "linkedinUrl": "https://linkedin.com/in/ananyasen",
  "createdAt": "2026-10-21T12:00:00",
  "skills": [],
  "certifications": []
}
```

---

### `PUT /api/v1/students/{id}`
Updates an existing student profile.

#### Request Body (`application/json`):
Same format as `POST /api/v1/students`.

#### Response:
`200 OK` with updated student object.

---

### `DELETE /api/v1/students/{id}`
Deletes a student and all associated skills and certifications (cascade delete).

#### Response:
`204 No Content`

---

## 2. Skills API

### `GET /api/v1/skills`
Returns all skills, optionally filtered by keyword or category.

#### Query Parameters:
| Parameter | Type | Valid Values |
|---|---|---|
| `keyword` | String | e.g. `Java`, `Python`, `AWS` |
| `category` | String | `PROGRAMMING_LANGUAGE`, `FRAMEWORK_LIBRARY`, `CLOUD_DEVOPS`, `DATA_AI`, `CYBERSECURITY`, `TOOLS_PLATFORMS`, `SOFT_SKILLS` |

#### Example Response (`200 OK`):
```json
[
  {
    "id": 1,
    "name": "Java",
    "category": "PROGRAMMING_LANGUAGE",
    "proficiencyLevel": "EXPERT",
    "rating": 5
  }
]
```

---

### `GET /api/v1/skills/student/{studentId}`
Returns all skills registered to a specific student.

---

### `POST /api/v1/skills`
Adds a technical skill to a student's portfolio.

#### Request Body (`application/json`):
```json
{
  "studentId": 1,
  "name": "Kubernetes",
  "category": "CLOUD_DEVOPS",
  "proficiencyLevel": "INTERMEDIATE",
  "rating": 3
}
```

#### Example Response (`201 Created`):
```json
{
  "id": 19,
  "name": "Kubernetes",
  "category": "CLOUD_DEVOPS",
  "proficiencyLevel": "INTERMEDIATE",
  "rating": 3
}
```

---

### `PUT /api/v1/skills/{id}`
Updates a skill's proficiency level or rating.

---

### `DELETE /api/v1/skills/{id}`
Deletes a skill from a student's portfolio.

#### Response:
`204 No Content`

---

## 3. Certifications API

### `GET /api/v1/certifications`
Returns certifications across the institution, optionally filtered by status or search keyword.

#### Query Parameters:
| Parameter | Type | Options |
|---|---|---|
| `status` | String | `PENDING`, `VERIFIED`, `REJECTED` |
| `search` | String | Keyword for title, issuer, or credential ID |

---

### `GET /api/v1/certifications/pending`
Returns only certifications awaiting faculty review (`PENDING`).

#### Example Response (`200 OK`):
```json
[
  {
    "id": 4,
    "title": "Google Cloud Associate Cloud Engineer",
    "issuingOrganization": "Google Cloud",
    "issueDate": "2026-02-10",
    "expiryDate": "2028-02-10",
    "credentialId": "GCP-ACE-318492",
    "credentialUrl": "https://google.accredible.com/gcp-ace-318492",
    "verificationStatus": "PENDING",
    "verifierNotes": null,
    "verifiedAt": null,
    "verifiedBy": null
  }
]
```

---

### `POST /api/v1/certifications`
Submits a new certification for verification (defaults to `PENDING` state).

#### Request Body (`application/json`):
```json
{
  "studentId": 2,
  "title": "HashiCorp Certified: Terraform Associate",
  "issuingOrganization": "HashiCorp",
  "issueDate": "2026-04-12",
  "expiryDate": "2028-04-12",
  "credentialId": "HASHI-TA-449102",
  "credentialUrl": "https://www.credly.com/badges/sample-id"
}
```

#### Example Response (`201 Created`):
```json
{
  "id": 10,
  "title": "HashiCorp Certified: Terraform Associate",
  "issuingOrganization": "HashiCorp",
  "issueDate": "2026-04-12",
  "expiryDate": "2028-04-12",
  "credentialId": "HASHI-TA-449102",
  "credentialUrl": "https://www.credly.com/badges/sample-id",
  "verificationStatus": "PENDING"
}
```

---

### `PATCH /api/v1/certifications/{id}/verify`
Faculty or Institutional Verifier action to endorse or reject a submission.

#### Request Body (`application/json`):
To **Approve & Verify**:
```json
{
  "status": "VERIFIED",
  "verifierNotes": "Credential verified on Credly and HashiCorp registry.",
  "verifiedBy": "Dr. R. K. Gupta (Faculty Coordinator)"
}
```

To **Reject**:
```json
{
  "status": "REJECTED",
  "verifierNotes": "Invalid credential ID; verification URL returned 404.",
  "verifiedBy": "Dr. R. K. Gupta (Faculty Coordinator)"
}
```

#### Example Response (`200 OK`):
```json
{
  "id": 10,
  "title": "HashiCorp Certified: Terraform Associate",
  "issuingOrganization": "HashiCorp",
  "verificationStatus": "VERIFIED",
  "verifierNotes": "Credential verified on Credly and HashiCorp registry.",
  "verifiedBy": "Dr. R. K. Gupta (Faculty Coordinator)",
  "verifiedAt": "2026-10-21T12:30:00"
}
```

---

### `DELETE /api/v1/certifications/{id}`
Deletes a certification record.

#### Response:
`204 No Content`

---

## 4. Analytics API

### `GET /api/v1/analytics/summary`
Returns institutional talent metrics, top skills, verification rate, and provider distributions.

#### Example Response (`200 OK`):
```json
{
  "totalStudents": 5,
  "totalCertifications": 9,
  "verifiedCertifications": 5,
  "pendingCertifications": 3,
  "rejectedCertifications": 1,
  "verificationRatePercentage": 55.6,
  "topSkills": {
    "Python": 3,
    "AWS": 1,
    "Docker": 1,
    "Java": 1,
    "Spring Boot": 1,
    "Ethical Hacking": 1,
    "React": 1
  },
  "certificationsByIssuer": {
    "Amazon Web Services": 1,
    "Cisco": 1,
    "CompTIA": 1,
    "Coursera": 1,
    "Google Cloud": 1,
    "Meta": 1
  },
  "skillsByCategory": {
    "Programming Language": 7,
    "Framework & Library": 4,
    "Tools & Platforms": 4,
    "Cloud & DevOps": 2,
    "Cybersecurity": 2,
    "Data Science & AI": 1
  }
}
```
