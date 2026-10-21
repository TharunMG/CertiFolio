package com.certifolio;

import com.certifolio.dto.StudentDto;
import com.certifolio.model.Student;
import com.certifolio.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    void testCreateAndRetrieveStudent() {
        StudentDto dto = new StudentDto();
        dto.setRegNo("TESTREG9999");
        dto.setFullName("Test Student");
        dto.setEmail("test.student@example.com");
        dto.setDepartment("Computer Science");
        dto.setYearOfStudy(3);
        dto.setCgpa(8.5);

        Student created = studentService.createStudent(dto);
        assertNotNull(created.getId());
        assertEquals("TESTREG9999", created.getRegNo());

        Student retrieved = studentService.getStudentById(created.getId());
        assertEquals("Test Student", retrieved.getFullName());
    }

    @Test
    void testDuplicateRegNoThrowsException() {
        StudentDto dto1 = new StudentDto();
        dto1.setRegNo("DUP123");
        dto1.setFullName("User One");
        dto1.setEmail("one@example.com");
        dto1.setDepartment("IT");
        dto1.setYearOfStudy(2);
        studentService.createStudent(dto1);

        StudentDto dto2 = new StudentDto();
        dto2.setRegNo("DUP123");
        dto2.setFullName("User Two");
        dto2.setEmail("two@example.com");
        dto2.setDepartment("IT");
        dto2.setYearOfStudy(2);

        assertThrows(IllegalArgumentException.class, () -> studentService.createStudent(dto2));
    }
}
