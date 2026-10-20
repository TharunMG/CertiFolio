package com.college.certifolio.controller;

import com.college.certifolio.dto.CertificateResponse;
import com.college.certifolio.dto.StudentSearchResponse;
import com.college.certifolio.dto.VerifyCertificateRequest;
import com.college.certifolio.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@Tag(name = "Faculty Audit & Talent Search", description = "Endpoints for faculty verification and placement talent search")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/certifications/pending")
    @Operation(summary = "Get list of pending student certificates requiring audit")
    public ResponseEntity<List<CertificateResponse>> getPendingCertifications() {
        return ResponseEntity.ok(facultyService.getPendingCertificates());
    }

    @PatchMapping("/certifications/{id}/verify")
    @Operation(summary = "Approve (VERIFIED) or reject (REJECTED) a certificate with faculty audit remarks")
    public ResponseEntity<CertificateResponse> verifyCertification(
            @PathVariable Long id,
            @Valid @RequestBody VerifyCertificateRequest request) {
        return ResponseEntity.ok(facultyService.verifyCertificate(id, request));
    }

    @GetMapping("/students/search")
    @Operation(summary = "Search talent pool by skill name, department, and minimum CGPA")
    public ResponseEntity<List<StudentSearchResponse>> searchStudents(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) Double minCgpa) {
        return ResponseEntity.ok(facultyService.searchStudents(skill, dept, minCgpa));
    }
}
