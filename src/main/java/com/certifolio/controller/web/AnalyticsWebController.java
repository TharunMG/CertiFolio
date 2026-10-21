package com.certifolio.controller.web;

import com.certifolio.dto.AnalyticsDto;
import com.certifolio.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analytics")
public class AnalyticsWebController {

    private final AnalyticsService analyticsService;

    public AnalyticsWebController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public String dashboard(Model model) {
        AnalyticsDto analytics = analyticsService.getAnalyticsSummary();
        model.addAttribute("analytics", analytics);
        return "analytics/dashboard";
    }
}
