package com.certifolio.dto;

import java.util.Map;

public class AnalyticsDto {

    private long totalStudents;
    private long totalCertifications;
    private long verifiedCertifications;
    private long pendingCertifications;
    private long rejectedCertifications;
    private double verificationRatePercentage;
    private Map<String, Long> topSkills;
    private Map<String, Long> certificationsByIssuer;
    private Map<String, Long> skillsByCategory;

    public AnalyticsDto() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalCertifications() {
        return totalCertifications;
    }

    public void setTotalCertifications(long totalCertifications) {
        this.totalCertifications = totalCertifications;
    }

    public long getVerifiedCertifications() {
        return verifiedCertifications;
    }

    public void setVerifiedCertifications(long verifiedCertifications) {
        this.verifiedCertifications = verifiedCertifications;
    }

    public long getPendingCertifications() {
        return pendingCertifications;
    }

    public void setPendingCertifications(long pendingCertifications) {
        this.pendingCertifications = pendingCertifications;
    }

    public long getRejectedCertifications() {
        return rejectedCertifications;
    }

    public void setRejectedCertifications(long rejectedCertifications) {
        this.rejectedCertifications = rejectedCertifications;
    }

    public double getVerificationRatePercentage() {
        return verificationRatePercentage;
    }

    public void setVerificationRatePercentage(double verificationRatePercentage) {
        this.verificationRatePercentage = verificationRatePercentage;
    }

    public Map<String, Long> getTopSkills() {
        return topSkills;
    }

    public void setTopSkills(Map<String, Long> topSkills) {
        this.topSkills = topSkills;
    }

    public Map<String, Long> getCertificationsByIssuer() {
        return certificationsByIssuer;
    }

    public void setCertificationsByIssuer(Map<String, Long> certificationsByIssuer) {
        this.certificationsByIssuer = certificationsByIssuer;
    }

    public Map<String, Long> getSkillsByCategory() {
        return skillsByCategory;
    }

    public void setSkillsByCategory(Map<String, Long> skillsByCategory) {
        this.skillsByCategory = skillsByCategory;
    }
}
