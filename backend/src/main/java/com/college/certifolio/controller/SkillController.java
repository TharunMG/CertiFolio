package com.college.certifolio.controller;

import com.college.certifolio.dto.SkillDto;
import com.college.certifolio.repository.SkillRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Master Skills Catalog", description = "Catalog of standard tech skills")
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    @Operation(summary = "List all skills in the master catalog")
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        List<SkillDto> skills = skillRepository.findAll()
                .stream()
                .map(s -> new SkillDto(s.getId(), s.getSkillName(), s.getCategory().name()))
                .toList();
        return ResponseEntity.ok(skills);
    }
}
