package com.certifolio.controller.web;

import com.certifolio.dto.CertificationDto;
import com.certifolio.model.Certification;
import com.certifolio.model.VerificationStatus;
import com.certifolio.service.CertificationService;
import com.certifolio.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/certifications")
public class CertificationWebController {

    private final CertificationService certificationService;
    private final StudentService studentService;

    public CertificationWebController(CertificationService certificationService, StudentService studentService) {
        this.certificationService = certificationService;
        this.studentService = studentService;
    }

    @GetMapping
    public String listCertifications(
            @RequestParam(required = false) VerificationStatus status,
            @RequestParam(required = false) String search,
            Model model) {

        List<Certification> list;
        if (status != null) {
            list = certificationService.getCertificationsByStatus(status);
        } else if (search != null && !search.trim().isEmpty()) {
            list = certificationService.searchCertifications(search);
        } else {
            list = certificationService.getAllCertifications();
        }

        model.addAttribute("certifications", list);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        return "certifications/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long studentId, Model model) {
        CertificationDto dto = new CertificationDto();
        if (studentId != null) {
            dto.setStudentId(studentId);
        }
        model.addAttribute("certificationDto", dto);
        model.addAttribute("students", studentService.getAllStudents());
        return "certifications/form";
    }

    @PostMapping("/save")
    public String saveCertification(
            @Valid @ModelAttribute("certificationDto") CertificationDto certificationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            return "certifications/form";
        }

        if (certificationDto.getId() != null) {
            certificationService.updateCertification(certificationDto.getId(), certificationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Certification updated successfully.");
        } else {
            certificationService.addCertification(certificationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Certification submitted for verification!");
        }

        return "redirect:/students/" + certificationDto.getStudentId() + "/portfolio";
    }

    @GetMapping("/delete/{id}")
    public String deleteCertification(@PathVariable Long id,
                                      @RequestParam(required = false) Long studentId,
                                      RedirectAttributes redirectAttributes) {
        certificationService.deleteCertification(id);
        redirectAttributes.addFlashAttribute("successMessage", "Certification removed.");
        if (studentId != null) {
            return "redirect:/students/" + studentId + "/portfolio";
        }
        return "redirect:/certifications";
    }
}
