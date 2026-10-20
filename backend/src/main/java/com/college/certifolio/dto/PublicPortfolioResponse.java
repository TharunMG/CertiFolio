package com.college.certifolio.dto;

import java.util.List;

public record PublicPortfolioResponse(
    String rollNumber,
    String fullName,
    String department,
    Integer yearOfStudy,
    Double cgpa,
    String githubUrl,
    String linkedinUrl,
    List<StudentSkillDto> skills,
    List<CertificateResponse> verifiedCertificates
) {}
