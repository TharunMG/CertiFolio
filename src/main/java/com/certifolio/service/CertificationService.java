package com.certifolio.service;

import com.certifolio.dto.CertificationDto;
import com.certifolio.dto.VerificationDecisionDto;
import com.certifolio.exception.ResourceNotFoundException;
import com.certifolio.model.Certification;
import com.certifolio.model.Student;
import com.certifolio.model.VerificationStatus;
import com.certifolio.repository.CertificationRepository;
import com.certifolio.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final StudentRepository studentRepository;

    public CertificationService(CertificationRepository certificationRepository, StudentRepository studentRepository) {
        this.certificationRepository = certificationRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Certification getCertificationById(Long id) {
        return certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Certification> getCertificationsByStudentId(Long studentId) {
        return certificationRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Certification> getCertificationsByStatus(VerificationStatus status) {
        return certificationRepository.findByVerificationStatusOrderByIssueDateDesc(status);
    }

    @Transactional(readOnly = true)
    public List<Certification> getPendingCertifications() {
        return certificationRepository.findByVerificationStatusOrderByIssueDateDesc(VerificationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Certification> searchCertifications(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return certificationRepository.findAll();
        }
        return certificationRepository.searchCertifications(keyword.trim());
    }

    public Certification addCertification(CertificationDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));

        Certification cert = new Certification();
        cert.setTitle(dto.getTitle().trim());
        cert.setIssuingOrganization(dto.getIssuingOrganization().trim());
        cert.setIssueDate(dto.getIssueDate());
        cert.setExpiryDate(dto.getExpiryDate());
        cert.setCredentialId(dto.getCredentialId());
        cert.setCredentialUrl(dto.getCredentialUrl());
        cert.setVerificationStatus(VerificationStatus.PENDING);
        cert.setStudent(student);

        student.getCertifications().add(cert);
        return certificationRepository.save(cert);
    }

    public Certification updateCertification(Long id, CertificationDto dto) {
        Certification cert = getCertificationById(id);
        cert.setTitle(dto.getTitle().trim());
        cert.setIssuingOrganization(dto.getIssuingOrganization().trim());
        cert.setIssueDate(dto.getIssueDate());
        cert.setExpiryDate(dto.getExpiryDate());
        cert.setCredentialId(dto.getCredentialId());
        cert.setCredentialUrl(dto.getCredentialUrl());
        return certificationRepository.save(cert);
    }

    public Certification verifyCertification(Long id, VerificationDecisionDto decision) {
        Certification cert = getCertificationById(id);
        cert.setVerificationStatus(decision.getStatus());
        cert.setVerifierNotes(decision.getVerifierNotes());
        cert.setVerifiedBy(decision.getVerifiedBy() != null ? decision.getVerifiedBy() : "Institutional Verifier");
        cert.setVerifiedAt(LocalDateTime.now());
        return certificationRepository.save(cert);
    }

    public void deleteCertification(Long id) {
        Certification cert = getCertificationById(id);
        certificationRepository.delete(cert);
    }
}
