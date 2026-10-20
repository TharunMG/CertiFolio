package com.college.certifolio.service;

import com.college.certifolio.entity.*;
import com.college.certifolio.enums.*;
import com.college.certifolio.repository.*;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final CertificationRepository certificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public DataInitializer(UserRepository userRepository,
                           SkillRepository skillRepository,
                           StudentProfileRepository studentProfileRepository,
                           StudentSkillRepository studentSkillRepository,
                           CertificationRepository certificationRepository,
                           PasswordEncoder passwordEncoder,
                           FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.certificationRepository = certificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedSkills();
        seedFaculty();
        seedStudents();
    }

    private void seedSkills() {
        if (skillRepository.count() > 0) {
            return;
        }

        List<Skill> masterSkills = List.of(
                // Backend
                Skill.builder().skillName("Java").category(SkillCategory.BACKEND).build(),
                Skill.builder().skillName("Spring Boot").category(SkillCategory.BACKEND).build(),
                Skill.builder().skillName("Python").category(SkillCategory.BACKEND).build(),
                Skill.builder().skillName("Node.js").category(SkillCategory.BACKEND).build(),
                Skill.builder().skillName("Go").category(SkillCategory.BACKEND).build(),

                // Frontend
                Skill.builder().skillName("React").category(SkillCategory.FRONTEND).build(),
                Skill.builder().skillName("Vue.js").category(SkillCategory.FRONTEND).build(),
                Skill.builder().skillName("Angular").category(SkillCategory.FRONTEND).build(),
                Skill.builder().skillName("TypeScript").category(SkillCategory.FRONTEND).build(),
                Skill.builder().skillName("Tailwind CSS").category(SkillCategory.FRONTEND).build(),

                // Database
                Skill.builder().skillName("MySQL").category(SkillCategory.DATABASE).build(),
                Skill.builder().skillName("PostgreSQL").category(SkillCategory.DATABASE).build(),
                Skill.builder().skillName("MongoDB").category(SkillCategory.DATABASE).build(),
                Skill.builder().skillName("Redis").category(SkillCategory.DATABASE).build(),

                // Cloud & DevOps
                Skill.builder().skillName("Docker").category(SkillCategory.CLOUD).build(),
                Skill.builder().skillName("Kubernetes").category(SkillCategory.CLOUD).build(),
                Skill.builder().skillName("AWS").category(SkillCategory.CLOUD).build(),
                Skill.builder().skillName("Microsoft Azure").category(SkillCategory.CLOUD).build(),

                // Tools
                Skill.builder().skillName("Git & GitHub").category(SkillCategory.TOOLS).build(),
                Skill.builder().skillName("Linux / Bash").category(SkillCategory.TOOLS).build()
        );

        skillRepository.saveAll(masterSkills);
        log.info("Pre-loaded 20 standard catalog skills successfully.");
    }

    private void seedFaculty() {
        if (userRepository.findByEmail("faculty@college.edu").isPresent()) {
            return;
        }

        User faculty = User.builder()
                .email("faculty@college.edu")
                .passwordHash(passwordEncoder.encode("faculty123"))
                .role(Role.ROLE_FACULTY)
                .build();
        userRepository.save(faculty);
        log.info("Created default faculty account: faculty@college.edu / faculty123");
    }

    private void seedStudents() {
        if (userRepository.findByEmail("student@college.edu").isPresent()) {
            return;
        }

        // Create sample certificate proof PDF file
        String samplePdfName = "sample_certificate_23CS042.pdf";
        createSamplePdfIfNotExists(samplePdfName, "NPTEL Online Certification — Programming in Java (Elite+Gold)");

        String sampleAwsPdfName = "sample_aws_23CS042.pdf";
        createSamplePdfIfNotExists(sampleAwsPdfName, "AWS Certified Cloud Practitioner Official Certificate");

        String sampleCourseraPdfName = "sample_coursera_23CS042.pdf";
        createSamplePdfIfNotExists(sampleCourseraPdfName, "Coursera Full-Stack Web Development Specialization");

        // Student 1: Arun Kumar (Roll: 23CS042)
        User studentUser1 = User.builder()
                .email("student@college.edu")
                .passwordHash(passwordEncoder.encode("student123"))
                .role(Role.ROLE_STUDENT)
                .build();
        studentUser1 = userRepository.save(studentUser1);

        StudentProfile arun = StudentProfile.builder()
                .user(studentUser1)
                .rollNumber("23CS042")
                .fullName("Arun Kumar")
                .department("CSE")
                .yearOfStudy(2)
                .cgpa(8.8)
                .githubUrl("https://github.com/arunkumar")
                .linkedinUrl("https://linkedin.com/in/arunkumar")
                .build();
        arun = studentProfileRepository.save(arun);

        // Attach Skills
        attachSkill(arun, "Java", Proficiency.ADVANCED, "https://github.com/arunkumar/java-algorithms", true);
        attachSkill(arun, "Spring Boot", Proficiency.INTERMEDIATE, "https://github.com/arunkumar/certifolio-api", true);
        attachSkill(arun, "React", Proficiency.INTERMEDIATE, "https://github.com/arunkumar/certifolio-ui", true);
        attachSkill(arun, "MySQL", Proficiency.INTERMEDIATE, null, false);

        // Attach Certifications
        Certification cert1 = Certification.builder()
                .studentProfile(arun)
                .title("Programming in Java (Elite+Gold)")
                .issuer("NPTEL")
                .credentialId("NPTEL23CS4289")
                .issueDate(LocalDate.of(2025, 4, 15))
                .filePath(samplePdfName)
                .status(VerificationStatus.VERIFIED)
                .remarks("Score: 92% Elite+Gold. Verified against NPTEL central roll registry.")
                .verifiedAt(LocalDateTime.now().minusDays(10))
                .build();
        certificationRepository.save(cert1);

        Certification cert2 = Certification.builder()
                .studentProfile(arun)
                .title("AWS Certified Cloud Practitioner")
                .issuer("AWS")
                .credentialId("AWS-CCP-98124")
                .issueDate(LocalDate.of(2025, 8, 20))
                .filePath(sampleAwsPdfName)
                .status(VerificationStatus.VERIFIED)
                .remarks("Valid until 2028. Verified on Credly digital credentials.")
                .verifiedAt(LocalDateTime.now().minusDays(5))
                .build();
        certificationRepository.save(cert2);

        Certification cert3 = Certification.builder()
                .studentProfile(arun)
                .title("Full-Stack Web Development with React")
                .issuer("Coursera")
                .credentialId("COURSERA-FS-7892")
                .issueDate(LocalDate.of(2025, 10, 1))
                .filePath(sampleCourseraPdfName)
                .status(VerificationStatus.PENDING)
                .remarks(null)
                .build();
        certificationRepository.save(cert3);

        // Student 2: Priya Sharma (Roll: 23CS015)
        User studentUser2 = User.builder()
                .email("priya@college.edu")
                .passwordHash(passwordEncoder.encode("student123"))
                .role(Role.ROLE_STUDENT)
                .build();
        studentUser2 = userRepository.save(studentUser2);

        StudentProfile priya = StudentProfile.builder()
                .user(studentUser2)
                .rollNumber("23CS015")
                .fullName("Priya Sharma")
                .department("CSE")
                .yearOfStudy(2)
                .cgpa(9.3)
                .githubUrl("https://github.com/priyasharma")
                .linkedinUrl("https://linkedin.com/in/priyasharma")
                .build();
        priya = studentProfileRepository.save(priya);

        attachSkill(priya, "React", Proficiency.ADVANCED, "https://github.com/priya/react-dashboards", true);
        attachSkill(priya, "TypeScript", Proficiency.ADVANCED, "https://github.com/priya/ts-enterprise", true);
        attachSkill(priya, "Docker", Proficiency.INTERMEDIATE, null, false);

        String sampleDockerPdf = "sample_docker_23CS015.pdf";
        createSamplePdfIfNotExists(sampleDockerPdf, "Docker Certified Associate Certificate — Priya Sharma");

        Certification cert4 = Certification.builder()
                .studentProfile(priya)
                .title("Docker Certified Associate")
                .issuer("Mirantis / Docker")
                .credentialId("DCA-2025-015")
                .issueDate(LocalDate.of(2025, 9, 10))
                .filePath(sampleDockerPdf)
                .status(VerificationStatus.PENDING)
                .remarks(null)
                .build();
        certificationRepository.save(cert4);

        // Student 3: Rahul Verma (Roll: 23IT028)
        User studentUser3 = User.builder()
                .email("rahul@college.edu")
                .passwordHash(passwordEncoder.encode("student123"))
                .role(Role.ROLE_STUDENT)
                .build();
        studentUser3 = userRepository.save(studentUser3);

        StudentProfile rahul = StudentProfile.builder()
                .user(studentUser3)
                .rollNumber("23IT028")
                .fullName("Rahul Verma")
                .department("IT")
                .yearOfStudy(2)
                .cgpa(7.9)
                .githubUrl("https://github.com/rahulverma")
                .linkedinUrl("https://linkedin.com/in/rahulverma")
                .build();
        rahul = studentProfileRepository.save(rahul);

        attachSkill(rahul, "Python", Proficiency.INTERMEDIATE, "https://github.com/rahul/python-data", true);
        attachSkill(rahul, "MongoDB", Proficiency.INTERMEDIATE, null, false);

        log.info("Initialized sample students (Arun Kumar 23CS042, Priya Sharma 23CS015, Rahul Verma 23IT028).");
    }

    private void attachSkill(StudentProfile profile, String skillName, Proficiency proficiency, String projectLink, boolean endorsed) {
        skillRepository.findBySkillNameIgnoreCase(skillName).ifPresent(skill -> {
            StudentSkill ss = StudentSkill.builder()
                    .studentProfile(profile)
                    .skill(skill)
                    .proficiency(proficiency)
                    .projectLink(projectLink)
                    .endorsed(endorsed)
                    .build();
            studentSkillRepository.save(ss);
        });
    }

    private void createSamplePdfIfNotExists(String filename, String titleText) {
        try {
            Path target = fileStorageService.getStorageLocation().resolve(filename);
            if (!Files.exists(target)) {
                Document doc = new Document();
                PdfWriter.getInstance(doc, new FileOutputStream(target.toFile()));
                doc.open();
                doc.add(new Paragraph("OFFICIAL PROOF OF CERTIFICATION"));
                doc.add(new Paragraph("======================================"));
                doc.add(new Paragraph("Certificate Title: " + titleText));
                doc.add(new Paragraph("Issued By: Accredited Certification Body"));
                doc.add(new Paragraph("Verification Hash: " + java.util.UUID.randomUUID()));
                doc.add(new Paragraph("Timestamp: " + LocalDateTime.now()));
                doc.close();
            }
        } catch (Exception e) {
            log.warn("Could not create sample PDF {}: {}", filename, e.getMessage());
        }
    }
}
