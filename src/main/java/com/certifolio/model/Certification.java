package com.certifolio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Certification title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Issuing organization is required")
    @Column(nullable = false, length = 100)
    private String issuingOrganization;

    @NotNull(message = "Issue date is required")
    @Column(nullable = false)
    private LocalDate issueDate;

    @Column
    private LocalDate expiryDate;

    @Column(length = 100)
    private String credentialId;

    @Column(length = 255)
    private String credentialUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String verifierNotes;

    @Column
    private LocalDateTime verifiedAt;

    @Column(length = 100)
    private String verifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"certifications", "skills"})
    private Student student;

    public Certification() {
    }

    public Certification(String title, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate, String credentialId, String credentialUrl) {
        this.title = title;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.credentialId = credentialId;
        this.credentialUrl = credentialUrl;
        this.verificationStatus = VerificationStatus.PENDING;
    }

    public Certification(String title, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate, String credentialId, String credentialUrl, VerificationStatus verificationStatus, Student student) {
        this.title = title;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.credentialId = credentialId;
        this.credentialUrl = credentialUrl;
        this.verificationStatus = verificationStatus;
        this.student = student;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerifierNotes() {
        return verifierNotes;
    }

    public void setVerifierNotes(String verifierNotes) {
        this.verifierNotes = verifierNotes;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
