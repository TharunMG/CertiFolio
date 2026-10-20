package com.college.certifolio.dto;

import jakarta.validation.constraints.NotNull;

public record AddSkillRequest(
    @NotNull(message = "Skill ID is required")
    Long skillId,

    @NotNull(message = "Proficiency is required")
    String proficiency,

    String projectLink
) {}
