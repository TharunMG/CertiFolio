package com.college.certifolio.service;

import com.college.certifolio.dto.*;
import com.college.certifolio.entity.*;
import com.college.certifolio.enums.Proficiency;
import com.college.certifolio.enums.VerificationStatus;
import com.college.certifolio.exception.BadRequestException;
import com.college.certifolio.exception.ResourceNotFoundException;
import com.college.certifolio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final CertificationRepository certificationRepository;
    private final FileStorageService fileStorageService;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          SkillRepository skillRepository,
                          StudentSkillRepository studentSkillRepository,
                          CertificationRepository certificationRepository,
                          FileStorageService fileStorageService) {
        this.studentProfileRepository = studentProfileRepository;
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.certificationRepository = certificationRepository;
        this.fileStorageService = fileStorageService;
    }

    public StudentProfile getProfile(Long studentId) {
        return studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for id: " + studentId));
    }

    public StudentProfile getProfileByUserId(Long userId) {
        return studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user id: " + userId));
    }

    @Transactional(readOnly = true)
    public List<StudentSkillDto> getStudentSkills(Long studentId) {
        StudentProfile profile = getProfile(studentId);
        return studentSkillRepository.findByStudentProfileId(profile.getId())
                .stream()
                .map(this::mapToSkillDto)
                .toList();
    }

    @Transactional
    public StudentSkillDto addSkill(Long studentId, AddSkillRequest request) {
        StudentProfile profile = getProfile(studentId);
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + request.skillId()));

        if (studentSkillRepository.existsByStudentProfileIdAndSkillId(profile.getId(), skill.getId())) {
            throw new BadRequestException("Skill '" + skill.getSkillName() + "' is already attached to this student profile");
        }

        Proficiency proficiency;
        try {
            proficiency = Proficiency.valueOf(request.proficiency().toUpperCase());
        } catch (IllegalArgumentException e) {
            proficiency = Proficiency.BEGINNER;
        }

        StudentSkill studentSkill = StudentSkill.builder()
                .studentProfile(profile)
                .skill(skill)
                .proficiency(proficiency)
                .projectLink(request.projectLink())
                .endorsed(false)
                .build();

        studentSkill = studentSkillRepository.save(studentSkill);
        return mapToSkillDto(studentSkill);
    }

    @Transactional
    public void deleteSkill(Long studentId, Long studentSkillId) {
        StudentSkill studentSkill = studentSkillRepository.findById(studentSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Student skill not found with id: " + studentSkillId));

        if (!studentSkill.getStudentProfile().getId().equals(studentId)) {
            throw new BadRequestException("This skill does not belong to the requested student");
        }

        studentSkillRepository.delete(studentSkill);
    }

    @Transactional
    public CertificateResponse uploadCertificate(Long studentId,
                                                String title,
                                                String issuer,
                                                String credentialId,
                                                LocalDate issueDate,
                                                MultipartFile file) {
        StudentProfile profile = getProfile(studentId);

        String storedFilename = fileStorageService.storeFile(file, profile.getRollNumber());

        Certification certification = Certification.builder()
                .studentProfile(profile)
                .title(title)
                .issuer(issuer)
                .credentialId(credentialId)
                .issueDate(issueDate != null ? issueDate : LocalDate.now())
                .filePath(storedFilename)
                .status(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        certification = certificationRepository.save(certification);
        return mapToCertResponse(certification);
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> getCertificates(Long studentId) {
        StudentProfile profile = getProfile(studentId);
        return certificationRepository.findByStudentProfileId(profile.getId())
                .stream()
                .map(this::mapToCertResponse)
                .toList();
    }

    public StudentSkillDto mapToSkillDto(StudentSkill ss) {
        return new StudentSkillDto(
                ss.getId(),
                ss.getSkill().getId(),
                ss.getSkill().getSkillName(),
                ss.getSkill().getCategory().name(),
                ss.getProficiency().name(),
                ss.getProjectLink(),
                ss.getEndorsed()
        );
    }

    public CertificateResponse mapToCertResponse(Certification cert) {
        String roll = cert.getStudentProfile() != null ? cert.getStudentProfile().getRollNumber() : "N/A";
        String name = cert.getStudentProfile() != null ? cert.getStudentProfile().getFullName() : "N/A";
        String dept = cert.getStudentProfile() != null ? cert.getStudentProfile().getDepartment() : "N/A";
        Long profileId = cert.getStudentProfile() != null ? cert.getStudentProfile().getId() : null;

        String fileUrl = "/api/files/" + cert.getFilePath();

        return new CertificateResponse(
                cert.getId(),
                profileId,
                roll,
                name,
                dept,
                cert.getTitle(),
                cert.getIssuer(),
                cert.getCredentialId(),
                cert.getIssueDate() != null ? cert.getIssueDate().toString() : null,
                cert.getFilePath(),
                fileUrl,
                cert.getStatus().name(),
                cert.getRemarks(),
                cert.getVerifiedAt() != null ? cert.getVerifiedAt().toString() : null,
                cert.getCreatedAt() != null ? cert.getCreatedAt().toString() : null
        );
    }
}
