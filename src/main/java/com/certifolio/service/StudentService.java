package com.certifolio.service;

import com.certifolio.dto.StudentDto;
import com.certifolio.exception.ResourceNotFoundException;
import com.certifolio.model.Student;
import com.certifolio.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Student getStudentByRegNo(String regNo) {
        return studentRepository.findByRegNo(regNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with Reg No: " + regNo));
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(query.trim());
    }

    @Transactional(readOnly = true)
    public List<Student> findByDepartment(String department) {
        return studentRepository.findByDepartmentIgnoreCase(department);
    }

    @Transactional(readOnly = true)
    public List<Student> findStudentsBySkill(String skillName) {
        return studentRepository.findStudentsBySkillName(skillName);
    }

    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return studentRepository.findAllDepartments();
    }

    public Student createStudent(StudentDto dto) {
        if (studentRepository.existsByRegNo(dto.getRegNo())) {
            throw new IllegalArgumentException("Student with registration number " + dto.getRegNo() + " already exists.");
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + dto.getEmail() + " already exists.");
        }

        Student student = new Student();
        copyDtoToEntity(dto, student);
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentDto dto) {
        Student student = getStudentById(id);

        if (!student.getRegNo().equalsIgnoreCase(dto.getRegNo()) && studentRepository.existsByRegNo(dto.getRegNo())) {
            throw new IllegalArgumentException("Registration number " + dto.getRegNo() + " is already taken.");
        }
        if (!student.getEmail().equalsIgnoreCase(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email " + dto.getEmail() + " is already in use.");
        }

        copyDtoToEntity(dto, student);
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    private void copyDtoToEntity(StudentDto dto, Student student) {
        student.setRegNo(dto.getRegNo().trim().toUpperCase());
        student.setFullName(dto.getFullName().trim());
        student.setEmail(dto.getEmail().trim().toLowerCase());
        student.setDepartment(dto.getDepartment().trim());
        student.setYearOfStudy(dto.getYearOfStudy());
        student.setCgpa(dto.getCgpa());
        student.setBio(dto.getBio());
        student.setGithubUrl(dto.getGithubUrl());
        student.setLinkedinUrl(dto.getLinkedinUrl());
    }
}
