package com.college.certifolio.entity;

import com.college.certifolio.enums.VerificationStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String issuer;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(length = 1000)
    private String remarks;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Certification() {
    }

    public Certification(Long id, StudentProfile studentProfile, String title, String issuer, String credentialId, LocalDate issueDate, String filePath, VerificationStatus status, String remarks, LocalDateTime verifiedAt, LocalDateTime createdAt) {
        this.id = id;
        this.studentProfile = studentProfile;
        this.title = title;
        this.issuer = issuer;
        this.credentialId = credentialId;
        this.issueDate = issueDate;
        this.filePath = filePath;
        this.status = (status != null) ? status : VerificationStatus.PENDING;
        this.remarks = remarks;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = VerificationStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private StudentProfile studentProfile;
        private String title;
        private String issuer;
        private String credentialId;
        private LocalDate issueDate;
        private String filePath;
        private VerificationStatus status = VerificationStatus.PENDING;
        private String remarks;
        private LocalDateTime verifiedAt;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder studentProfile(StudentProfile studentProfile) {
            this.studentProfile = studentProfile;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder credentialId(String credentialId) {
            this.credentialId = credentialId;
            return this;
        }

        public Builder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder status(VerificationStatus status) {
            this.status = status;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder verifiedAt(LocalDateTime verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Certification build() {
            return new Certification(id, studentProfile, title, issuer, credentialId, issueDate, filePath, status, remarks, verifiedAt, createdAt);
        }
    }
}
