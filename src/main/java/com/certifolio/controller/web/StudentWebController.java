package com.certifolio.controller.web;

import com.certifolio.dto.CertificationDto;
import com.certifolio.dto.SkillDto;
import com.certifolio.dto.StudentDto;
import com.certifolio.model.ProficiencyLevel;
import com.certifolio.model.SkillCategory;
import com.certifolio.model.Student;
import com.certifolio.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentWebController {

    private final StudentService studentService;

    public StudentWebController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String skill,
            Model model) {

        List<Student> students;
        if (skill != null && !skill.trim().isEmpty()) {
            students = studentService.findStudentsBySkill(skill);
        } else if (department != null && !department.trim().isEmpty()) {
            students = studentService.findByDepartment(department);
        } else if (search != null && !search.trim().isEmpty()) {
            students = studentService.searchStudents(search);
        } else {
            students = studentService.getAllStudents();
        }

        model.addAttribute("students", students);
        model.addAttribute("departments", studentService.getAllDepartments());
        model.addAttribute("search", search);
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedSkill", skill);
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("studentDto")) {
            model.addAttribute("studentDto", new StudentDto());
        }
        return "students/form";
    }

    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("studentDto") StudentDto studentDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "students/form";
        }

        try {
            if (studentDto.getId() != null) {
                studentService.updateStudent(studentDto.getId(), studentDto);
                redirectAttributes.addFlashAttribute("successMessage", "Student profile updated successfully!");
            } else {
                studentService.createStudent(studentDto);
                redirectAttributes.addFlashAttribute("successMessage", "Student registered successfully!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/students/new";
        }

        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setRegNo(student.getRegNo());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setDepartment(student.getDepartment());
        dto.setYearOfStudy(student.getYearOfStudy());
        dto.setCgpa(student.getCgpa());
        dto.setBio(student.getBio());
        dto.setGithubUrl(student.getGithubUrl());
        dto.setLinkedinUrl(student.getLinkedinUrl());

        model.addAttribute("studentDto", dto);
        return "students/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student profile deleted successfully.");
        return "redirect:/students";
    }

    @GetMapping("/{id}/portfolio")
    public String viewPortfolio(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        
        // Models for inline modals to quickly add skill or certification from student's portfolio view
        SkillDto skillDto = new SkillDto();
        skillDto.setStudentId(student.getId());
        model.addAttribute("skillDto", skillDto);

        CertificationDto certDto = new CertificationDto();
        certDto.setStudentId(student.getId());
        model.addAttribute("certDto", certDto);

        model.addAttribute("categories", SkillCategory.values());
        model.addAttribute("levels", ProficiencyLevel.values());
        return "students/portfolio";
    }

    @GetMapping("/reg/{regNo}/portfolio")
    public String viewPortfolioByRegNo(@PathVariable String regNo, Model model) {
        Student student = studentService.getStudentByRegNo(regNo);
        return "redirect:/students/" + student.getId() + "/portfolio";
    }
}
