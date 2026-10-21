package com.certifolio.dto;

import com.certifolio.model.VerificationStatus;
import jakarta.validation.constraints.NotNull;

public class VerificationDecisionDto {

    @NotNull(message = "Status decision is required (VERIFIED or REJECTED)")
    private VerificationStatus status;

    private String verifierNotes;
    private String verifiedBy = "Institutional Verifier";

    public VerificationDecisionDto() {
    }

    public VerificationDecisionDto(VerificationStatus status, String verifierNotes, String verifiedBy) {
        this.status = status;
        this.verifierNotes = verifierNotes;
        this.verifiedBy = verifiedBy;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getVerifierNotes() {
        return verifierNotes;
    }

    public void setVerifierNotes(String verifierNotes) {
        this.verifierNotes = verifierNotes;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
}
