package com.college.certifolio.entity;

import com.college.certifolio.enums.SkillCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", nullable = false, unique = true)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;

    public Skill() {
    }

    public Skill(Long id, String skillName, SkillCategory category) {
        this.id = id;
        this.skillName = skillName;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String skillName;
        private SkillCategory category;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder skillName(String skillName) {
            this.skillName = skillName;
            return this;
        }

        public Builder category(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Skill build() {
            return new Skill(id, skillName, category);
        }
    }
}
