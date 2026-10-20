package com.college.certifolio.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCertificateRequest(
    @NotBlank(message = "Status is required (VERIFIED or REJECTED)")
    String status,

    String remarks
) {}
