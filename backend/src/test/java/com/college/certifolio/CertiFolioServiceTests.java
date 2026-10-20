package com.college.certifolio;

import com.college.certifolio.dto.CertificateResponse;
import com.college.certifolio.dto.PublicPortfolioResponse;
import com.college.certifolio.dto.StudentSearchResponse;
import com.college.certifolio.dto.VerifyCertificateRequest;
import com.college.certifolio.entity.*;
import com.college.certifolio.enums.*;
import com.college.certifolio.exception.BadRequestException;
import com.college.certifolio.repository.*;
import com.college.certifolio.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CertiFolioServiceTests {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test 1: Faculty verification state machine transitions to VERIFIED and sets audit timestamp")
    void testVerifyCertificate_transitionsStateAndSetsAuditTimestamp() {
        StudentProfile student = studentProfileRepository.findByRollNumber("23CS042")
                .orElseThrow();

        Certification cert = Certification.builder()
                .studentProfile(student)
                .title("Advanced Algorithms Specialization")
                .issuer("Coursera")
                .credentialId("ALG-998877")
                .issueDate(LocalDate.now())
                .filePath("sample_algorithms.pdf")
                .status(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        cert = certificationRepository.save(cert);

        VerifyCertificateRequest verifyReq = new VerifyCertificateRequest(
                "VERIFIED",
                "Successfully cross-checked with Coursera registrar. High distinction."
        );

        CertificateResponse response = facultyService.verifyCertificate(cert.getId(), verifyReq);

        assertNotNull(response);
        assertEquals("VERIFIED", response.status());
        assertEquals("Successfully cross-checked with Coursera registrar. High distinction.", response.remarks());
        assertNotNull(response.verifiedAt(), "Audit timestamp must be recorded upon faculty approval");
    }

    @Test
    @DisplayName("Test 2: Architectural Safeguard - Public portfolio strictly excludes PENDING and REJECTED certificates")
    void testPublicPortfolio_strictlyFiltersOnlyVerifiedCertificates() {
        StudentProfile student = studentProfileRepository.findByRollNumber("23CS042")
                .orElseThrow();

        // Create a rejected cert
        Certification rejectedCert = Certification.builder()
                .studentProfile(student)
                .title("Unverified Bootcamp Certificate")
                .issuer("Unknown Org")
                .credentialId("FAKE-123")
                .issueDate(LocalDate.now())
                .filePath("fake_cert.pdf")
                .status(VerificationStatus.REJECTED)
                .remarks("Unaccredited organization")
                .createdAt(LocalDateTime.now())
                .build();
        certificationRepository.save(rejectedCert);

        // Fetch public portfolio
        PublicPortfolioResponse portfolio = portfolioService.getPublicPortfolio("23CS042");

        assertNotNull(portfolio);
        assertFalse(portfolio.verifiedCertificates().isEmpty(), "Student should have verified certificates");

        // Verify that EVERY certificate in the public response has status VERIFIED
        for (CertificateResponse c : portfolio.verifiedCertificates()) {
            assertEquals("VERIFIED", c.status(), "Public portfolio must only show VERIFIED certificates!");
            assertNotEquals("Unverified Bootcamp Certificate", c.title(), "REJECTED certificate must NOT appear in public portfolio!");
        }
    }

    @Test
    @DisplayName("Test 3: FileStorageService validates MIME types and rejects unauthorized formats")
    void testFileStorage_validatesMimeTypesAndRejectsInvalid() {
        // Valid PDF file
        MockMultipartFile validPdf = new MockMultipartFile(
                "file", "proof.pdf", "application/pdf", "Dummy PDF content".getBytes()
        );
        String savedPdf = fileStorageService.storeFile(validPdf, "23CS042");
        assertNotNull(savedPdf);
        assertTrue(savedPdf.endsWith(".pdf"));

        // Invalid executable or script file
        MockMultipartFile invalidExe = new MockMultipartFile(
                "file", "malware.exe", "application/x-msdownload", "binary content".getBytes()
        );

        assertThrows(BadRequestException.class, () -> {
            fileStorageService.storeFile(invalidExe, "23CS042");
        }, "FileStorageService should reject non-PDF/PNG/JPEG files");
    }

    @Test
    @DisplayName("Test 4: Faculty talent search query accurately filters by skill, department, and CGPA")
    void testSearchStudents_filtersBySkillDepartmentAndCgpa() {
        // Search CSE students with React and min CGPA 8.5
        List<StudentSearchResponse> results = facultyService.searchStudents("React", "CSE", 8.5);

        assertNotNull(results);
        assertFalse(results.isEmpty(), "Should find CSE students with React and CGPA >= 8.5");

        for (StudentSearchResponse s : results) {
            assertEquals("CSE", s.department());
            assertTrue(s.cgpa() >= 8.5, "CGPA should be >= 8.5");
            assertTrue(s.skills().stream().anyMatch(sk -> sk.equalsIgnoreCase("React")), "Student must possess React skill");
        }
    }
}
