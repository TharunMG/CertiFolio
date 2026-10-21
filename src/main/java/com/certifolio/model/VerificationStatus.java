package com.certifolio.model;

public enum VerificationStatus {
    PENDING("Pending Verification", "warning"),
    VERIFIED("Verified", "success"),
    REJECTED("Rejected", "danger");

    private final String label;
    private final String badgeColor;

    VerificationStatus(String label, String badgeColor) {
        this.label = label;
        this.badgeColor = badgeColor;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeColor() {
        return badgeColor;
    }
}
