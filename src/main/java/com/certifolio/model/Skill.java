package com.certifolio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Skill name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Skill category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SkillCategory category;

    @NotNull(message = "Proficiency level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProficiencyLevel proficiencyLevel = ProficiencyLevel.INTERMEDIATE;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating = 3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"skills", "certifications"})
    private Student student;

    public Skill() {
    }

    public Skill(String name, SkillCategory category, ProficiencyLevel proficiencyLevel, Integer rating) {
        this.name = name;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
        this.rating = rating;
    }

    public Skill(String name, SkillCategory category, ProficiencyLevel proficiencyLevel, Integer rating, Student student) {
        this.name = name;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
        this.rating = rating;
        this.student = student;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public ProficiencyLevel getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
