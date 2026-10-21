package com.certifolio.controller.api;

import com.certifolio.dto.StudentDto;
import com.certifolio.model.Student;
import com.certifolio.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Student Portfolio & Profile Management APIs")
public class StudentApiController {

    private final StudentService studentService;

    public StudentApiController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(summary = "List all students or search with query/department")
    public ResponseEntity<List<Student>> getStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String skill) {

        if (skill != null && !skill.trim().isEmpty()) {
            return ResponseEntity.ok(studentService.findStudentsBySkill(skill));
        }
        if (department != null && !department.trim().isEmpty()) {
            return ResponseEntity.ok(studentService.findByDepartment(department));
        }
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(studentService.searchStudents(search));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student profile by ID")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/reg/{regNo}")
    @Operation(summary = "Get student profile by Registration Number")
    public ResponseEntity<Student> getStudentByRegNo(@PathVariable String regNo) {
        return ResponseEntity.ok(studentService.getStudentByRegNo(regNo));
    }

    @PostMapping
    @Operation(summary = "Register a new student")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentDto dto) {
        Student created = studentService.createStudent(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student profile")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student profile")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
