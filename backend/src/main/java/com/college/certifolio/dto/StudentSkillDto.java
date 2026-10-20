package com.college.certifolio.dto;

public record StudentSkillDto(
    Long id,
    Long skillId,
    String skillName,
    String category,
    String proficiency,
    String projectLink,
    Boolean endorsed
) {}
