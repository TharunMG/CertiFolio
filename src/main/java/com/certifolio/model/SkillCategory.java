package com.certifolio.model;

public enum SkillCategory {
    PROGRAMMING_LANGUAGE("Programming Language"),
    FRAMEWORK_LIBRARY("Framework & Library"),
    CLOUD_DEVOPS("Cloud & DevOps"),
    DATA_AI("Data Science & AI"),
    CYBERSECURITY("Cybersecurity"),
    TOOLS_PLATFORMS("Tools & Platforms"),
    SOFT_SKILLS("Professional & Soft Skills");

    private final String displayName;

    SkillCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
