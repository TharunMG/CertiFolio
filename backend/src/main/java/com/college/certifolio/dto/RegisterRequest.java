package com.college.certifolio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    String role, // "STUDENT" or "FACULTY"

    // Optional student profile details if registering as student
    String rollNumber,
    String fullName,
    String department,
    Integer yearOfStudy,
    Double cgpa,
    String githubUrl,
    String linkedinUrl
) {}
