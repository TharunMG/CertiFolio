package com.college.certifolio.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, unique = true)
    private String rollNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String department;

    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;

    @Column(nullable = false)
    private Double cgpa;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentSkill> studentSkills = new ArrayList<>();

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    public StudentProfile() {
    }

    public StudentProfile(Long id, String rollNumber, String fullName, String department, Integer yearOfStudy, Double cgpa, String githubUrl, String linkedinUrl, User user, List<StudentSkill> studentSkills, List<Certification> certifications) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.cgpa = cgpa;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.user = user;
        if (studentSkills != null) this.studentSkills = studentSkills;
        if (certifications != null) this.certifications = certifications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<StudentSkill> getStudentSkills() {
        return studentSkills;
    }

    public void setStudentSkills(List<StudentSkill> studentSkills) {
        this.studentSkills = studentSkills;
    }

    public List<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String rollNumber;
        private String fullName;
        private String department;
        private Integer yearOfStudy;
        private Double cgpa;
        private String githubUrl;
        private String linkedinUrl;
        private User user;
        private List<StudentSkill> studentSkills = new ArrayList<>();
        private List<Certification> certifications = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder rollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder yearOfStudy(Integer yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
            return this;
        }

        public Builder cgpa(Double cgpa) {
            this.cgpa = cgpa;
            return this;
        }

        public Builder githubUrl(String githubUrl) {
            this.githubUrl = githubUrl;
            return this;
        }

        public Builder linkedinUrl(String linkedinUrl) {
            this.linkedinUrl = linkedinUrl;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder studentSkills(List<StudentSkill> studentSkills) {
            this.studentSkills = studentSkills;
            return this;
        }

        public Builder certifications(List<Certification> certifications) {
            this.certifications = certifications;
            return this;
        }

        public StudentProfile build() {
            return new StudentProfile(id, rollNumber, fullName, department, yearOfStudy, cgpa, githubUrl, linkedinUrl, user, studentSkills, certifications);
        }
    }
}
