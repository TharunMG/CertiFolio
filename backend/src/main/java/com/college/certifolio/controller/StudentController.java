package com.college.certifolio.controller;

import com.college.certifolio.dto.*;
import com.college.certifolio.entity.StudentProfile;
import com.college.certifolio.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Operations", description = "Student skill management and certificate uploads")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Get student profile details")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long id) {
        StudentProfile p = studentService.getProfile(id);
        Map<String, Object> response = Map.of(
                "id", p.getId(),
                "rollNumber", p.getRollNumber(),
                "fullName", p.getFullName(),
                "department", p.getDepartment(),
                "yearOfStudy", p.getYearOfStudy(),
                "cgpa", p.getCgpa(),
                "githubUrl", p.getGithubUrl() != null ? p.getGithubUrl() : "",
                "linkedinUrl", p.getLinkedinUrl() != null ? p.getLinkedinUrl() : ""
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/skills")
    @Operation(summary = "Get list of skills attached to student")
    public ResponseEntity<List<StudentSkillDto>> getSkills(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentSkills(id));
    }

    @PostMapping("/{id}/skills")
    @Operation(summary = "Attach a catalog skill to student profile")
    public ResponseEntity<StudentSkillDto> addSkill(
            @PathVariable Long id,
            @Valid @RequestBody AddSkillRequest request) {
        return new ResponseEntity<>(studentService.addSkill(id, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    @Operation(summary = "Remove a skill from student profile")
    public ResponseEntity<Map<String, String>> deleteSkill(
            @PathVariable Long id,
            @PathVariable Long skillId) {
        studentService.deleteSkill(id, skillId);
        return ResponseEntity.ok(Map.of("message", "Skill removed successfully"));
    }

    @PostMapping(value = "/{id}/certifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a certification proof (PDF/PNG/JPEG)")
    public ResponseEntity<CertificateResponse> uploadCertificate(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("issuer") String issuer,
            @RequestParam(value = "credentialId", required = false) String credentialId,
            @RequestParam(value = "issueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate) {
        CertificateResponse cert = studentService.uploadCertificate(id, title, issuer, credentialId, issueDate, file);
        return new ResponseEntity<>(cert, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/certifications")
    @Operation(summary = "Get all certification submissions for student")
    public ResponseEntity<List<CertificateResponse>> getCertificates(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getCertificates(id));
    }
}
