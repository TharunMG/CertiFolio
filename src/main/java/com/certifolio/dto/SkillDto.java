package com.certifolio.dto;

import com.certifolio.model.ProficiencyLevel;
import com.certifolio.model.SkillCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SkillDto {

    private Long id;

    @NotBlank(message = "Skill name is required")
    private String name;

    @NotNull(message = "Skill category is required")
    private SkillCategory category;

    @NotNull(message = "Proficiency level is required")
    private ProficiencyLevel proficiencyLevel = ProficiencyLevel.INTERMEDIATE;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating = 3;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    public SkillDto() {
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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
