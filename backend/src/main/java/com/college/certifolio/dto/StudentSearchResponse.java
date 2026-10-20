package com.college.certifolio.dto;

import java.util.List;

public record StudentSearchResponse(
    Long id,
    String rollNumber,
    String fullName,
    String department,
    Integer yearOfStudy,
    Double cgpa,
    String githubUrl,
    String linkedinUrl,
    List<String> skills,
    Long verifiedCertCount
) {}
