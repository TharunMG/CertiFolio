package com.college.certifolio.dto;

public record CertificateResponse(
    Long id,
    Long studentProfileId,
    String studentRollNumber,
    String studentName,
    String studentDepartment,
    String title,
    String issuer,
    String credentialId,
    String issueDate,
    String filePath,
    String fileUrl,
    String status,
    String remarks,
    String verifiedAt,
    String createdAt
) {}
