package com.college.certifolio.controller;

import com.college.certifolio.dto.PublicPortfolioResponse;
import com.college.certifolio.service.PdfExportService;
import com.college.certifolio.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@Tag(name = "Public Portfolio", description = "Public portfolio viewing and OpenPDF export")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PdfExportService pdfExportService;

    public PortfolioController(PortfolioService portfolioService,
                               PdfExportService pdfExportService) {
        this.portfolioService = portfolioService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/{rollNo}")
    @Operation(summary = "Get verified public portfolio for a student by roll number")
    public ResponseEntity<PublicPortfolioResponse> getPublicPortfolio(@PathVariable String rollNo) {
        return ResponseEntity.ok(portfolioService.getPublicPortfolio(rollNo));
    }

    @GetMapping("/{rollNo}/pdf")
    @Operation(summary = "Generate and download official PDF portfolio transcript (OpenPDF)")
    public ResponseEntity<byte[]> downloadPortfolioPdf(@PathVariable String rollNo) {
        byte[] pdfBytes = pdfExportService.generatePortfolioPdf(rollNo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", rollNo.toUpperCase() + "_Portfolio.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
