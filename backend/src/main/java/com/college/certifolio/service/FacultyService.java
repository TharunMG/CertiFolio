package com.college.certifolio.service;

import com.college.certifolio.dto.CertificateResponse;
import com.college.certifolio.dto.StudentSearchResponse;
import com.college.certifolio.dto.VerifyCertificateRequest;
import com.college.certifolio.entity.Certification;
import com.college.certifolio.entity.StudentProfile;
import com.college.certifolio.entity.StudentSkill;
import com.college.certifolio.enums.VerificationStatus;
import com.college.certifolio.exception.BadRequestException;
import com.college.certifolio.exception.ResourceNotFoundException;
import com.college.certifolio.repository.CertificationRepository;
import com.college.certifolio.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacultyService {

    private final CertificationRepository certificationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentService studentService;

    public FacultyService(CertificationRepository certificationRepository,
                          StudentProfileRepository studentProfileRepository,
                          StudentService studentService) {
        this.certificationRepository = certificationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentService = studentService;
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> getPendingCertificates() {
        return certificationRepository.findByStatusOrderByCreatedAtDesc(VerificationStatus.PENDING)
                .stream()
                .map(studentService::mapToCertResponse)
                .toList();
    }

    @Transactional
    public CertificateResponse verifyCertificate(Long certId, VerifyCertificateRequest request) {
        Certification cert = certificationRepository.findById(certId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found for id: " + certId));

        VerificationStatus newStatus;
        try {
            newStatus = VerificationStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid verification status: " + request.status() + ". Allowed: VERIFIED, REJECTED");
        }

        if (newStatus == VerificationStatus.PENDING) {
            throw new BadRequestException("Status can only be updated to VERIFIED or REJECTED");
        }

        cert.setStatus(newStatus);
        cert.setRemarks(request.remarks());
        cert.setVerifiedAt(LocalDateTime.now());

        cert = certificationRepository.save(cert);
        return studentService.mapToCertResponse(cert);
    }

    @Transactional(readOnly = true)
    public List<StudentSearchResponse> searchStudents(String skill, String dept, Double minCgpa) {
        List<StudentProfile> profiles = studentProfileRepository.searchStudents(
                (skill != null && !skill.isBlank()) ? skill.trim() : null,
                (dept != null && !dept.isBlank()) ? dept.trim() : null,
                minCgpa
        );

        return profiles.stream().map(sp -> {
            List<String> skillNames = sp.getStudentSkills()
                    .stream()
                    .map(ss -> ss.getSkill().getSkillName())
                    .toList();

            long verifiedCount = certificationRepository.countByStudentProfileIdAndStatus(sp.getId(), VerificationStatus.VERIFIED);

            return new StudentSearchResponse(
                    sp.getId(),
                    sp.getRollNumber(),
                    sp.getFullName(),
                    sp.getDepartment(),
                    sp.getYearOfStudy(),
                    sp.getCgpa(),
                    sp.getGithubUrl(),
                    sp.getLinkedinUrl(),
                    skillNames,
                    verifiedCount
            );
        }).toList();
    }
}
