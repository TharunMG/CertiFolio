package com.certifolio.controller.web;

import com.certifolio.dto.VerificationDecisionDto;
import com.certifolio.model.Certification;
import com.certifolio.model.VerificationStatus;
import com.certifolio.service.CertificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/audit")
public class VerificationWebController {

    private final CertificationService certificationService;

    public VerificationWebController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public String auditConsole(Model model) {
        List<Certification> pendingList = certificationService.getPendingCertifications();
        List<Certification> verifiedList = certificationService.getCertificationsByStatus(VerificationStatus.VERIFIED);
        List<Certification> rejectedList = certificationService.getCertificationsByStatus(VerificationStatus.REJECTED);

        model.addAttribute("pendingList", pendingList);
        model.addAttribute("verifiedList", verifiedList);
        model.addAttribute("rejectedList", rejectedList);
        return "verifier/audit";
    }

    @PostMapping("/decision/{id}")
    public String decide(
            @PathVariable Long id,
            @RequestParam VerificationStatus status,
            @RequestParam(required = false, defaultValue = "") String verifierNotes,
            @RequestParam(required = false, defaultValue = "Institutional Verifier") String verifiedBy,
            RedirectAttributes redirectAttributes) {

        VerificationDecisionDto decision = new VerificationDecisionDto(status, verifierNotes, verifiedBy);
        certificationService.verifyCertification(id, decision);

        if (status == VerificationStatus.VERIFIED) {
            redirectAttributes.addFlashAttribute("successMessage", "Certification has been verified and stamped!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Certification was marked as rejected with feedback.");
        }

        return "redirect:/audit";
    }
}
