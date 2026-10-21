package com.certifolio.controller.api;

import com.certifolio.dto.CertificationDto;
import com.certifolio.dto.VerificationDecisionDto;
import com.certifolio.model.Certification;
import com.certifolio.model.VerificationStatus;
import com.certifolio.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certifications")
@Tag(name = "Certifications", description = "Student Certification & Verification Lifecycle APIs")
public class CertificationApiController {

    private final CertificationService certificationService;

    public CertificationApiController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    @Operation(summary = "Get all certifications or filter by status/search")
    public ResponseEntity<List<Certification>> getCertifications(
            @RequestParam(required = false) VerificationStatus status,
            @RequestParam(required = false) String search) {

        if (status != null) {
            return ResponseEntity.ok(certificationService.getCertificationsByStatus(status));
        }
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(certificationService.searchCertifications(search));
        }
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get certification by ID")
    public ResponseEntity<Certification> getCertificationById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending certifications awaiting institutional verification")
    public ResponseEntity<List<Certification>> getPendingCertifications() {
        return ResponseEntity.ok(certificationService.getPendingCertifications());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all certifications for a student")
    public ResponseEntity<List<Certification>> getCertificationsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(certificationService.getCertificationsByStudentId(studentId));
    }

    @PostMapping
    @Operation(summary = "Submit a certification for a student")
    public ResponseEntity<Certification> submitCertification(@Valid @RequestBody CertificationDto dto) {
        Certification created = certificationService.addCertification(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update certification details")
    public ResponseEntity<Certification> updateCertification(@PathVariable Long id, @Valid @RequestBody CertificationDto dto) {
        return ResponseEntity.ok(certificationService.updateCertification(id, dto));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verify or reject certification (Faculty / Verifier action)")
    public ResponseEntity<Certification> verifyCertification(
            @PathVariable Long id,
            @Valid @RequestBody VerificationDecisionDto decision) {
        return ResponseEntity.ok(certificationService.verifyCertification(id, decision));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete certification")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }
}
