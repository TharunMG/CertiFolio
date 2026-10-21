package com.certifolio.controller.api;

import com.certifolio.dto.AnalyticsDto;
import com.certifolio.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Talent Intelligence & Verification Metrics APIs")
public class AnalyticsApiController {

    private final AnalyticsService analyticsService;

    public AnalyticsApiController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get full registry analytics summary")
    public ResponseEntity<AnalyticsDto> getSummary() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }
}
