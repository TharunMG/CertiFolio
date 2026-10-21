package com.certifolio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Column(nullable = false, unique = true, length = 50)
    private String regNo;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Department is required")
    @Column(nullable = false, length = 80)
    private String department;

    @NotNull(message = "Year of study is required")
    @Column(nullable = false)
    private Integer yearOfStudy;

    @Column
    private Double cgpa;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String githubUrl;

    @Column(length = 255)
    private String linkedinUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("student")
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("student")
    private List<Certification> certifications = new ArrayList<>();

    public Student() {
    }

    public Student(String regNo, String fullName, String email, String department, Integer yearOfStudy, Double cgpa, String bio, String githubUrl, String linkedinUrl) {
        this.regNo = regNo;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.cgpa = cgpa;
        this.bio = bio;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setStudent(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setStudent(null);
    }

    public void addCertification(Certification certification) {
        certifications.add(certification);
        certification.setStudent(this);
    }

    public void removeCertification(Certification certification) {
        certifications.remove(certification);
        certification.setStudent(null);
    }

    public long getVerifiedCertificationsCount() {
        return certifications.stream()
                .filter(c -> c.getVerificationStatus() == VerificationStatus.VERIFIED)
                .count();
    }

    public long getPendingCertificationsCount() {
        return certifications.stream()
                .filter(c -> c.getVerificationStatus() == VerificationStatus.PENDING)
                .count();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }
}
