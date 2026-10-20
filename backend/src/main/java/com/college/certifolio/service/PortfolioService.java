package com.college.certifolio.service;

import com.college.certifolio.dto.CertificateResponse;
import com.college.certifolio.dto.PublicPortfolioResponse;
import com.college.certifolio.dto.StudentSkillDto;
import com.college.certifolio.entity.StudentProfile;
import com.college.certifolio.enums.VerificationStatus;
import com.college.certifolio.exception.ResourceNotFoundException;
import com.college.certifolio.repository.CertificationRepository;
import com.college.certifolio.repository.StudentProfileRepository;
import com.college.certifolio.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortfolioService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final CertificationRepository certificationRepository;
    private final StudentService studentService;

    public PortfolioService(StudentProfileRepository studentProfileRepository,
                            StudentSkillRepository studentSkillRepository,
                            CertificationRepository certificationRepository,
                            StudentService studentService) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.certificationRepository = certificationRepository;
        this.studentService = studentService;
    }

    @Transactional(readOnly = true)
    public PublicPortfolioResponse getPublicPortfolio(String rollNumber) {
        String cleanRoll = rollNumber.trim().toUpperCase();
        StudentProfile profile = studentProfileRepository.findByRollNumber(cleanRoll)
                .orElseThrow(() -> new ResourceNotFoundException("No student portfolio found with roll number: " + rollNumber));

        List<StudentSkillDto> skills = studentSkillRepository.findByStudentProfileId(profile.getId())
                .stream()
                .map(studentService::mapToSkillDto)
                .toList();

        // Architectural Safeguard: strictly filter by status = VERIFIED at the SQL level
        List<CertificateResponse> verifiedCerts = certificationRepository
                .findByStudentProfileRollNumberAndStatusOrderByIssueDateDesc(cleanRoll, VerificationStatus.VERIFIED)
                .stream()
                .map(studentService::mapToCertResponse)
                .toList();

        return new PublicPortfolioResponse(
                profile.getRollNumber(),
                profile.getFullName(),
                profile.getDepartment(),
                profile.getYearOfStudy(),
                profile.getCgpa(),
                profile.getGithubUrl(),
                profile.getLinkedinUrl(),
                skills,
                verifiedCerts
        );
    }
}
