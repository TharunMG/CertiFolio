package com.certifolio.config;

import com.certifolio.model.*;
import com.certifolio.repository.CertificationRepository;
import com.certifolio.repository.SkillRepository;
import com.certifolio.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final CertificationRepository certificationRepository;

    public DataInitializer(StudentRepository studentRepository,
                           SkillRepository skillRepository,
                           CertificationRepository certificationRepository) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.certificationRepository = certificationRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() > 0) {
            return;
        }

        // Student 1: Aarav Sharma
        Student s1 = new Student(
                "REG2023CS0101",
                "Aarav Sharma",
                "aarav.sharma@certifolio.edu",
                "Computer Science & Engineering",
                4,
                9.25,
                "Passionate Cloud & Full-Stack Java Engineer. Open source contributor and campus tech lead.",
                "https://github.com/aaravsharma",
                "https://linkedin.com/in/aarav-sharma-dev"
        );
        studentRepository.save(s1);

        skillRepository.saveAll(List.of(
                new Skill("Java", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.EXPERT, 5, s1),
                new Skill("Spring Boot", SkillCategory.FRAMEWORK_LIBRARY, ProficiencyLevel.EXPERT, 5, s1),
                new Skill("AWS", SkillCategory.CLOUD_DEVOPS, ProficiencyLevel.ADVANCED, 4, s1),
                new Skill("Docker", SkillCategory.CLOUD_DEVOPS, ProficiencyLevel.ADVANCED, 4, s1),
                new Skill("PostgreSQL", SkillCategory.TOOLS_PLATFORMS, ProficiencyLevel.INTERMEDIATE, 4, s1)
        ));

        Certification c1_1 = new Certification(
                "AWS Certified Solutions Architect - Associate",
                "Amazon Web Services",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2028, 3, 15),
                "AWS-SAA-904128",
                "https://aws.amazon.com/verification",
                VerificationStatus.VERIFIED,
                s1
        );
        c1_1.setVerifiedBy("Dr. R. K. Gupta (Faculty Coordinator)");
        c1_1.setVerifiedAt(LocalDateTime.now().minusDays(30));
        c1_1.setVerifierNotes("Credential verified via AWS certification directory.");

        Certification c1_2 = new Certification(
                "Oracle Certified Professional: Java SE 17 Developer",
                "Oracle",
                LocalDate.of(2024, 11, 10),
                null,
                "OCP-JAVA-881249",
                "https://catalog-education.oracle.com/pls/certview",
                VerificationStatus.VERIFIED,
                s1
        );
        c1_2.setVerifiedBy("Dr. R. K. Gupta (Faculty Coordinator)");
        c1_2.setVerifiedAt(LocalDateTime.now().minusDays(45));
        c1_2.setVerifierNotes("Official Oracle verification certificate validated.");

        certificationRepository.saveAll(List.of(c1_1, c1_2));

        // Student 2: Priya Patel
        Student s2 = new Student(
                "REG2024AI0204",
                "Priya Patel",
                "priya.patel@certifolio.edu",
                "Artificial Intelligence & Data Science",
                3,
                8.90,
                "Deep Learning & NLP researcher. Working on medical image segmentation and LLM fine-tuning.",
                "https://github.com/priyapatel-ai",
                "https://linkedin.com/in/priya-patel-data"
        );
        studentRepository.save(s2);

        skillRepository.saveAll(List.of(
                new Skill("Python", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.EXPERT, 5, s2),
                new Skill("TensorFlow", SkillCategory.FRAMEWORK_LIBRARY, ProficiencyLevel.ADVANCED, 4, s2),
                new Skill("PyTorch", SkillCategory.FRAMEWORK_LIBRARY, ProficiencyLevel.ADVANCED, 4, s2),
                new Skill("Data Analysis & Pandas", SkillCategory.DATA_AI, ProficiencyLevel.ADVANCED, 4, s2)
        ));

        Certification c2_1 = new Certification(
                "DeepLearning.AI Deep Learning Specialization",
                "Coursera",
                LocalDate.of(2025, 1, 20),
                null,
                "COURSERA-DL-77319",
                "https://coursera.org/verify/COURSERA-DL-77319",
                VerificationStatus.VERIFIED,
                s2
        );
        c2_1.setVerifiedBy("Prof. S. Narayanan (HOD AI)");
        c2_1.setVerifiedAt(LocalDateTime.now().minusDays(10));
        c2_1.setVerifierNotes("Coursera certificate ID successfully verified.");

        Certification c2_2 = new Certification(
                "Google Cloud Associate Cloud Engineer",
                "Google Cloud",
                LocalDate.of(2026, 2, 10),
                LocalDate.of(2028, 2, 10),
                "GCP-ACE-318492",
                "https://google.accredible.com/gcp-ace-318492",
                VerificationStatus.PENDING,
                s2
        );
        certificationRepository.saveAll(List.of(c2_1, c2_2));

        // Student 3: Rohan Mehta
        Student s3 = new Student(
                "REG2024IT0315",
                "Rohan Mehta",
                "rohan.mehta@certifolio.edu",
                "Information Technology",
                3,
                8.45,
                "Frontend enthusiast crafting intuitive UI/UX with modern React, Next.js, and TypeScript.",
                "https://github.com/rohanmehta-dev",
                "https://linkedin.com/in/rohan-mehta-web"
        );
        studentRepository.save(s3);

        skillRepository.saveAll(List.of(
                new Skill("JavaScript", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.ADVANCED, 4, s3),
                new Skill("React", SkillCategory.FRAMEWORK_LIBRARY, ProficiencyLevel.ADVANCED, 4, s3),
                new Skill("TypeScript", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.INTERMEDIATE, 3, s3),
                new Skill("Tailwind CSS", SkillCategory.TOOLS_PLATFORMS, ProficiencyLevel.ADVANCED, 5, s3)
        ));

        Certification c3_1 = new Certification(
                "Meta Front-End Developer Professional Certificate",
                "Meta",
                LocalDate.of(2024, 8, 14),
                null,
                "META-FE-55421",
                "https://coursera.org/verify/professional-cert/META-FE-55421",
                VerificationStatus.VERIFIED,
                s3
        );
        c3_1.setVerifiedBy("Dr. R. K. Gupta (Faculty Coordinator)");
        c3_1.setVerifiedAt(LocalDateTime.now().minusDays(60));
        c3_1.setVerifierNotes("All 9 capstone courses validated.");

        Certification c3_2 = new Certification(
                "React Native Specialist Certification",
                "freeCodeCamp",
                LocalDate.of(2026, 3, 5),
                null,
                "FCC-RN-11823",
                "https://freecodecamp.org/certification/rohan/react-native",
                VerificationStatus.PENDING,
                s3
        );
        certificationRepository.saveAll(List.of(c3_1, c3_2));

        // Student 4: Sneha Rao
        Student s4 = new Student(
                "REG2023CS0188",
                "Sneha Rao",
                "sneha.rao@certifolio.edu",
                "Computer Science & Engineering",
                4,
                9.52,
                "Cybersecurity enthusiast and CTF player. Focused on penetration testing and threat analysis.",
                "https://github.com/sneharao-sec",
                "https://linkedin.com/in/sneha-rao-cyber"
        );
        studentRepository.save(s4);

        skillRepository.saveAll(List.of(
                new Skill("Ethical Hacking", SkillCategory.CYBERSECURITY, ProficiencyLevel.EXPERT, 5, s4),
                new Skill("Network Security", SkillCategory.CYBERSECURITY, ProficiencyLevel.ADVANCED, 4, s4),
                new Skill("Linux System Administration", SkillCategory.TOOLS_PLATFORMS, ProficiencyLevel.EXPERT, 5, s4),
                new Skill("Python", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.ADVANCED, 4, s4)
        ));

        Certification c4_1 = new Certification(
                "CompTIA Security+",
                "CompTIA",
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2027, 12, 1),
                "COMP-SEC-998822",
                "https://www.certmetrics.com/comptia/public/verification.aspx",
                VerificationStatus.VERIFIED,
                s4
        );
        c4_1.setVerifiedBy("Dr. R. K. Gupta (Faculty Coordinator)");
        c4_1.setVerifiedAt(LocalDateTime.now().minusDays(20));
        c4_1.setVerifierNotes("CompTIA verification badge verified.");
        certificationRepository.save(c4_1);

        // Student 5: Vikram Das
        Student s5 = new Student(
                "REG2025EC0402",
                "Vikram Das",
                "vikram.das@certifolio.edu",
                "Electronics & Communication",
                2,
                7.80,
                "Exploring embedded systems, IoT architecture, and edge computing.",
                "https://github.com/vikramdas-embedded",
                "https://linkedin.com/in/vikram-das-iot"
        );
        studentRepository.save(s5);

        skillRepository.saveAll(List.of(
                new Skill("C / Embedded C", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.ADVANCED, 4, s5),
                new Skill("IoT Networking", SkillCategory.TOOLS_PLATFORMS, ProficiencyLevel.INTERMEDIATE, 3, s5),
                new Skill("Python", SkillCategory.PROGRAMMING_LANGUAGE, ProficiencyLevel.BEGINNER, 2, s5)
        ));

        Certification c5_1 = new Certification(
                "Cisco Certified Network Associate (CCNA)",
                "Cisco",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2029, 1, 15),
                "CISCO-CCNA-601938",
                "https://cisco.com/go/verifycert",
                VerificationStatus.PENDING,
                s5
        );

        Certification c5_2 = new Certification(
                "Introductory Python Course",
                "Unknown Academy",
                LocalDate.of(2025, 6, 10),
                null,
                "INVALID-ID-000",
                "",
                VerificationStatus.REJECTED,
                s5
        );
        c5_2.setVerifiedBy("Prof. S. Narayanan (Verifier)");
        c5_2.setVerifiedAt(LocalDateTime.now().minusDays(5));
        c5_2.setVerifierNotes("Rejected: Verification URL is missing and institution could not validate credential ID.");

        certificationRepository.saveAll(List.of(c5_1, c5_2));
    }
}
