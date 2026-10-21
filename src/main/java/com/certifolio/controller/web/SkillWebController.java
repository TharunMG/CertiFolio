package com.certifolio.controller.web;

import com.certifolio.dto.SkillDto;
import com.certifolio.model.ProficiencyLevel;
import com.certifolio.model.Skill;
import com.certifolio.model.SkillCategory;
import com.certifolio.service.SkillService;
import com.certifolio.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/skills")
public class SkillWebController {

    private final SkillService skillService;
    private final StudentService studentService;

    public SkillWebController(SkillService skillService, StudentService studentService) {
        this.skillService = skillService;
        this.studentService = studentService;
    }

    @GetMapping
    public String skillMatrix(
            @RequestParam(required = false) SkillCategory category,
            @RequestParam(required = false) String search,
            Model model) {

        List<Skill> skills;
        if (category != null) {
            skills = skillService.getSkillsByCategory(category);
        } else if (search != null && !search.trim().isEmpty()) {
            skills = skillService.searchSkills(search);
        } else {
            skills = skillService.getAllSkills();
        }

        model.addAttribute("skills", skills);
        model.addAttribute("categories", SkillCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("search", search);
        return "skills/matrix";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long studentId, Model model) {
        SkillDto dto = new SkillDto();
        if (studentId != null) {
            dto.setStudentId(studentId);
        }
        model.addAttribute("skillDto", dto);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("categories", SkillCategory.values());
        model.addAttribute("levels", ProficiencyLevel.values());
        return "skills/form";
    }

    @PostMapping("/save")
    public String saveSkill(
            @Valid @ModelAttribute("skillDto") SkillDto skillDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("categories", SkillCategory.values());
            model.addAttribute("levels", ProficiencyLevel.values());
            return "skills/form";
        }

        if (skillDto.getId() != null) {
            skillService.updateSkill(skillDto.getId(), skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill updated successfully.");
        } else {
            skillService.addSkillToStudent(skillDto);
            redirectAttributes.addFlashAttribute("successMessage", "Skill added to student profile.");
        }

        return "redirect:/students/" + skillDto.getStudentId() + "/portfolio";
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id,
                              @RequestParam(required = false) Long studentId,
                              RedirectAttributes redirectAttributes) {
        skillService.deleteSkill(id);
        redirectAttributes.addFlashAttribute("successMessage", "Skill removed from profile.");
        if (studentId != null) {
            return "redirect:/students/" + studentId + "/portfolio";
        }
        return "redirect:/skills";
    }
}
