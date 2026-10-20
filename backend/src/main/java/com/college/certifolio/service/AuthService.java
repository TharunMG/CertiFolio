package com.college.certifolio.service;

import com.college.certifolio.config.JwtTokenProvider;
import com.college.certifolio.dto.AuthRequest;
import com.college.certifolio.dto.AuthResponse;
import com.college.certifolio.dto.RegisterRequest;
import com.college.certifolio.entity.StudentProfile;
import com.college.certifolio.entity.User;
import com.college.certifolio.enums.Role;
import com.college.certifolio.exception.BadRequestException;
import com.college.certifolio.repository.StudentProfileRepository;
import com.college.certifolio.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        Long studentId = null;
        String rollNumber = null;
        String fullName = null;

        if (user.getRole() == Role.ROLE_STUDENT) {
            StudentProfile profile = studentProfileRepository.findByUserId(user.getId()).orElse(null);
            if (profile != null) {
                studentId = profile.getId();
                rollNumber = profile.getRollNumber();
                fullName = profile.getFullName();
            }
        } else {
            fullName = user.getEmail().split("@")[0].toUpperCase();
        }

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name(), studentId, rollNumber, fullName);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered: " + normalizedEmail);
        }

        Role role = Role.ROLE_STUDENT;
        if (request.role() != null && request.role().equalsIgnoreCase("FACULTY")) {
            role = Role.ROLE_FACULTY;
        }

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        user = userRepository.save(user);

        Long studentId = null;
        String rollNumber = null;
        String fullName = request.fullName();

        if (role == Role.ROLE_STUDENT) {
            String roll = request.rollNumber() != null ? request.rollNumber().trim().toUpperCase() : "ROLL-" + System.currentTimeMillis();
            if (studentProfileRepository.existsByRollNumber(roll)) {
                throw new BadRequestException("Roll number is already registered: " + roll);
            }

            StudentProfile profile = StudentProfile.builder()
                    .user(user)
                    .rollNumber(roll)
                    .fullName(request.fullName() != null && !request.fullName().isBlank() ? request.fullName() : "Student " + roll)
                    .department(request.department() != null && !request.department().isBlank() ? request.department().toUpperCase() : "CSE")
                    .yearOfStudy(request.yearOfStudy() != null ? request.yearOfStudy() : 2)
                    .cgpa(request.cgpa() != null ? request.cgpa() : 8.0)
                    .githubUrl(request.githubUrl())
                    .linkedinUrl(request.linkedinUrl())
                    .build();

            profile = studentProfileRepository.save(profile);
            studentId = profile.getId();
            rollNumber = profile.getRollNumber();
            fullName = profile.getFullName();
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name(), studentId, rollNumber, fullName);
    }
}
