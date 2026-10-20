package com.college.certifolio.service;

import com.college.certifolio.dto.CertificateResponse;
import com.college.certifolio.dto.PublicPortfolioResponse;
import com.college.certifolio.dto.StudentSkillDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfExportService {

    private final PortfolioService portfolioService;

    public PdfExportService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public byte[] generatePortfolioPdf(String rollNumber) {
        PublicPortfolioResponse portfolio = portfolioService.getPublicPortfolio(rollNumber);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Color Palette
            Color primaryBlue = new Color(30, 58, 138); // #1E3A8A
            Color secondaryTeal = new Color(13, 148, 136); // #0D9488
            Color lightGray = new Color(243, 244, 246);
            Color darkText = new Color(31, 41, 55);

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, primaryBlue);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, primaryBlue);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, darkText);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, darkText);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, darkText);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

            // Header Banner
            Paragraph title = new Paragraph("CertiFolio", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            document.add(title);

            Paragraph tagline = new Paragraph("Verified Academic Skill & Credential Registry — Official Transcript", subtitleFont);
            tagline.setSpacingAfter(15);
            document.add(tagline);

            // Horizontal Divider
            Paragraph line = new Paragraph();
            line.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1.5f, 100, primaryBlue, Element.ALIGN_CENTER, -2)));
            line.setSpacingAfter(15);
            document.add(line);

            // Student Demographics Box
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{20f, 30f, 20f, 30f});

            addInfoCell(infoTable, "Full Name:", portfolio.fullName(), labelFont, valueFont);
            addInfoCell(infoTable, "Roll Number:", portfolio.rollNumber(), labelFont, valueFont);
            addInfoCell(infoTable, "Department:", portfolio.department(), labelFont, valueFont);
            addInfoCell(infoTable, "Year of Study:", portfolio.yearOfStudy() + " Year", labelFont, valueFont);
            addInfoCell(infoTable, "CGPA:", String.format("%.2f / 10.0", portfolio.cgpa()), labelFont, valueFont);
            addInfoCell(infoTable, "GitHub Profile:", (portfolio.githubUrl() != null ? portfolio.githubUrl() : "N/A"), labelFont, valueFont);

            infoTable.setSpacingAfter(15);
            document.add(infoTable);

            // Section 1: Endorsed Skills Matrix
            Paragraph skillsTitle = new Paragraph("1. TECHNICAL PROFICIENCIES & SKILL MATRIX", sectionFont);
            skillsTitle.setSpacingAfter(8);
            document.add(skillsTitle);

            if (portfolio.skills().isEmpty()) {
                Paragraph noSkills = new Paragraph("No skills registered yet.", valueFont);
                noSkills.setSpacingAfter(12);
                document.add(noSkills);
            } else {
                PdfPTable skillsTable = new PdfPTable(4);
                skillsTable.setWidthPercentage(100);
                skillsTable.setWidths(new float[]{30f, 25f, 25f, 20f});

                addTableHeaderCell(skillsTable, "Skill Name", primaryBlue, tableHeaderFont);
                addTableHeaderCell(skillsTable, "Category", primaryBlue, tableHeaderFont);
                addTableHeaderCell(skillsTable, "Proficiency", primaryBlue, tableHeaderFont);
                addTableHeaderCell(skillsTable, "Project Link", primaryBlue, tableHeaderFont);

                boolean alternate = false;
                for (StudentSkillDto skill : portfolio.skills()) {
                    Color bg = alternate ? lightGray : Color.WHITE;
                    addTableCell(skillsTable, skill.skillName(), bg, tableCellFont);
                    addTableCell(skillsTable, skill.category(), bg, tableCellFont);
                    addTableCell(skillsTable, skill.proficiency(), bg, tableCellFont);
                    addTableCell(skillsTable, (skill.projectLink() != null && !skill.projectLink().isBlank()) ? skill.projectLink() : "—", bg, tableCellFont);
                    alternate = !alternate;
                }
                skillsTable.setSpacingAfter(15);
                document.add(skillsTable);
            }

            // Section 2: Faculty-Verified Certifications
            Paragraph certsTitle = new Paragraph("2. FACULTY-VERIFIED CREDENTIALS & CERTIFICATIONS", sectionFont);
            certsTitle.setSpacingAfter(8);
            document.add(certsTitle);

            if (portfolio.verifiedCertificates().isEmpty()) {
                Paragraph noCerts = new Paragraph("No faculty-verified certifications on record at this time.", valueFont);
                noCerts.setSpacingAfter(15);
                document.add(noCerts);
            } else {
                PdfPTable certsTable = new PdfPTable(5);
                certsTable.setWidthPercentage(100);
                certsTable.setWidths(new float[]{28f, 18f, 18f, 18f, 18f});

                addTableHeaderCell(certsTable, "Certification Title", secondaryTeal, tableHeaderFont);
                addTableHeaderCell(certsTable, "Issuer", secondaryTeal, tableHeaderFont);
                addTableHeaderCell(certsTable, "Credential ID", secondaryTeal, tableHeaderFont);
                addTableHeaderCell(certsTable, "Status", secondaryTeal, tableHeaderFont);
                addTableHeaderCell(certsTable, "Audit / Remarks", secondaryTeal, tableHeaderFont);

                boolean alternate = false;
                for (CertificateResponse cert : portfolio.verifiedCertificates()) {
                    Color bg = alternate ? lightGray : Color.WHITE;
                    addTableCell(certsTable, cert.title(), bg, tableCellFont);
                    addTableCell(certsTable, cert.issuer(), bg, tableCellFont);
                    addTableCell(certsTable, cert.credentialId() != null ? cert.credentialId() : "N/A", bg, tableCellFont);
                    addTableCell(certsTable, "[VERIFIED]", bg, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(5, 150, 105)));
                    String remarks = (cert.remarks() != null && !cert.remarks().isBlank()) ? cert.remarks() : "Audited by Faculty";
                    addTableCell(certsTable, remarks, bg, tableCellFont);
                    alternate = !alternate;
                }
                certsTable.setSpacingAfter(20);
                document.add(certsTable);
            }

            // Authenticity & Verification Footer Box
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            PdfPCell footerCell = new PdfPCell();
            footerCell.setBackgroundColor(new Color(240, 249, 255)); // Light sky blue
            footerCell.setBorderColor(new Color(186, 230, 253));
            footerCell.setPadding(10);

            Paragraph auditNote = new Paragraph("Institutional Authenticity Guarantee:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, primaryBlue));
            Paragraph auditDetails = new Paragraph(
                    "This document was digitally compiled by the CertiFolio Registry engine on " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")) +
                    ". All listed credentials have been verified by accredited faculty members against verified document hashes.",
                    footerFont
            );
            footerCell.addElement(auditNote);
            footerCell.addElement(auditDetails);
            footerTable.addCell(footerCell);
            document.add(footerTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate portfolio PDF for roll number: " + rollNumber, e);
        }

        return out.toByteArray();
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value != null ? value : "—", valueFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addTableHeaderCell(PdfPTable table, String header, Color bgColor, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String content, Color bgColor, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "—", font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
