package com.certifolio.service;

import com.certifolio.dto.AnalyticsDto;
import com.certifolio.model.SkillCategory;
import com.certifolio.model.VerificationStatus;
import com.certifolio.repository.CertificationRepository;
import com.certifolio.repository.SkillRepository;
import com.certifolio.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final CertificationRepository certificationRepository;

    public AnalyticsService(StudentRepository studentRepository,
                            SkillRepository skillRepository,
                            CertificationRepository certificationRepository) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.certificationRepository = certificationRepository;
    }

    public AnalyticsDto getAnalyticsSummary() {
        AnalyticsDto dto = new AnalyticsDto();

        long totalStudents = studentRepository.count();
        long totalCerts = certificationRepository.count();
        long verifiedCerts = certificationRepository.countByVerificationStatus(VerificationStatus.VERIFIED);
        long pendingCerts = certificationRepository.countByVerificationStatus(VerificationStatus.PENDING);
        long rejectedCerts = certificationRepository.countByVerificationStatus(VerificationStatus.REJECTED);

        dto.setTotalStudents(totalStudents);
        dto.setTotalCertifications(totalCerts);
        dto.setVerifiedCertifications(verifiedCerts);
        dto.setPendingCertifications(pendingCerts);
        dto.setRejectedCertifications(rejectedCerts);

        double rate = totalCerts > 0 ? ((double) verifiedCerts / totalCerts) * 100.0 : 0.0;
        dto.setVerificationRatePercentage(Math.round(rate * 10.0) / 10.0);

        // Top Skills
        Map<String, Long> topSkills = new LinkedHashMap<>();
        List<Object[]> topSkillsRaw = skillRepository.findTopSkillsRaw();
        int skillLimit = 0;
        for (Object[] row : topSkillsRaw) {
            if (skillLimit++ >= 7) break;
            topSkills.put((String) row[0], ((Number) row[1]).longValue());
        }
        dto.setTopSkills(topSkills);

        // Certifications by Issuer
        Map<String, Long> certsByIssuer = new LinkedHashMap<>();
        List<Object[]> issuerRaw = certificationRepository.countCertificationsByIssuerRaw();
        int issuerLimit = 0;
        for (Object[] row : issuerRaw) {
            if (issuerLimit++ >= 6) break;
            certsByIssuer.put((String) row[0], ((Number) row[1]).longValue());
        }
        dto.setCertificationsByIssuer(certsByIssuer);

        // Skills by Category
        Map<String, Long> skillsByCategory = new LinkedHashMap<>();
        List<Object[]> categoryRaw = skillRepository.countSkillsByCategoryRaw();
        for (Object[] row : categoryRaw) {
            SkillCategory cat = (SkillCategory) row[0];
            skillsByCategory.put(cat.getDisplayName(), ((Number) row[1]).longValue());
        }
        dto.setSkillsByCategory(skillsByCategory);

        return dto;
    }
}
