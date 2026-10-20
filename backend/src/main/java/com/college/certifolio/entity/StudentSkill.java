package com.college.certifolio.entity;

import com.college.certifolio.enums.Proficiency;
import jakarta.persistence.*;

@Entity
@Table(name = "student_skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "skill_id"}))
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Proficiency proficiency;

    @Column(name = "project_link")
    private String projectLink;

    @Column(nullable = false)
    private Boolean endorsed = false;

    public StudentSkill() {
    }

    public StudentSkill(Long id, StudentProfile studentProfile, Skill skill, Proficiency proficiency, String projectLink, Boolean endorsed) {
        this.id = id;
        this.studentProfile = studentProfile;
        this.skill = skill;
        this.proficiency = proficiency;
        this.projectLink = projectLink;
        this.endorsed = (endorsed != null) ? endorsed : false;
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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Proficiency getProficiency() {
        return proficiency;
    }

    public void setProficiency(Proficiency proficiency) {
        this.proficiency = proficiency;
    }

    public String getProjectLink() {
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    public Boolean getEndorsed() {
        return endorsed;
    }

    public void setEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private StudentProfile studentProfile;
        private Skill skill;
        private Proficiency proficiency;
        private String projectLink;
        private Boolean endorsed = false;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder studentProfile(StudentProfile studentProfile) {
            this.studentProfile = studentProfile;
            return this;
        }

        public Builder skill(Skill skill) {
            this.skill = skill;
            return this;
        }

        public Builder proficiency(Proficiency proficiency) {
            this.proficiency = proficiency;
            return this;
        }

        public Builder projectLink(String projectLink) {
            this.projectLink = projectLink;
            return this;
        }

        public Builder endorsed(Boolean endorsed) {
            this.endorsed = endorsed;
            return this;
        }

        public StudentSkill build() {
            return new StudentSkill(id, studentProfile, skill, proficiency, projectLink, endorsed);
        }
    }
}
