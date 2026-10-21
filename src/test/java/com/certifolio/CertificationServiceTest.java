package com.certifolio;

import com.certifolio.dto.CertificationDto;
import com.certifolio.dto.StudentDto;
import com.certifolio.dto.VerificationDecisionDto;
import com.certifolio.model.Certification;
import com.certifolio.model.Student;
import com.certifolio.model.VerificationStatus;
import com.certifolio.service.CertificationService;
import com.certifolio.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CertificationServiceTest {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private StudentService studentService;

    @Test
    void testCertificationLifecycleAndVerification() {
        StudentDto studentDto = new StudentDto();
        studentDto.setRegNo("CERT_TEST_01");
        studentDto.setFullName("Certification Test Student");
        studentDto.setEmail("cert.test@example.com");
        studentDto.setDepartment("Cybersecurity");
        studentDto.setYearOfStudy(4);
        Student student = studentService.createStudent(studentDto);

        CertificationDto certDto = new CertificationDto();
        certDto.setStudentId(student.getId());
        certDto.setTitle("AWS Certified Cloud Practitioner");
        certDto.setIssuingOrganization("AWS");
        certDto.setIssueDate(LocalDate.now());
        certDto.setCredentialId("AWS-CCP-12345");
        certDto.setCredentialUrl("https://aws.amazon.com/verify");

        // Submit
        Certification submitted = certificationService.addCertification(certDto);
        assertNotNull(submitted.getId());
        assertEquals(VerificationStatus.PENDING, submitted.getVerificationStatus());

        // Verify
        VerificationDecisionDto approveDecision = new VerificationDecisionDto(
                VerificationStatus.VERIFIED,
                "Verified credential against AWS registry.",
                "Dr. Smith"
        );
        Certification verified = certificationService.verifyCertification(submitted.getId(), approveDecision);
        assertEquals(VerificationStatus.VERIFIED, verified.getVerificationStatus());
        assertEquals("Dr. Smith", verified.getVerifiedBy());
        assertNotNull(verified.getVerifiedAt());
    }
}
