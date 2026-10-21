package com.certifolio.controller.web;

import com.certifolio.dto.AnalyticsDto;
import com.certifolio.service.AnalyticsService;
import com.certifolio.service.CertificationService;
import com.certifolio.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AnalyticsService analyticsService;
    private final StudentService studentService;
    private final CertificationService certificationService;

    public HomeController(AnalyticsService analyticsService,
                          StudentService studentService,
                          CertificationService certificationService) {
        this.analyticsService = analyticsService;
        this.studentService = studentService;
        this.certificationService = certificationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        AnalyticsDto analytics = analyticsService.getAnalyticsSummary();
        model.addAttribute("analytics", analytics);
        model.addAttribute("recentStudents", studentService.getAllStudents().stream().limit(5).toList());
        model.addAttribute("pendingCertifications", certificationService.getPendingCertifications().stream().limit(5).toList());
        return "index";
    }
}
