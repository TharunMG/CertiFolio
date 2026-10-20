package com.college.certifolio.dto;

public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String email,
    String role,
    Long studentProfileId,
    String rollNumber,
    String fullName
) {
    public AuthResponse(String token, Long userId, String email, String role, Long studentProfileId, String rollNumber, String fullName) {
        this(token, "Bearer", userId, email, role, studentProfileId, rollNumber, fullName);
    }
}
