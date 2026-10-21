package com.certifolio.controller.api;

import com.certifolio.dto.SkillDto;
import com.certifolio.model.Skill;
import com.certifolio.model.SkillCategory;
import com.certifolio.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@Tag(name = "Skills", description = "Student Skill Registry APIs")
public class SkillApiController {

    private final SkillService skillService;

    public SkillApiController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "Get all registered skills or filter by category/keyword")
    public ResponseEntity<List<Skill>> getSkills(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SkillCategory category) {

        if (category != null) {
            return ResponseEntity.ok(skillService.getSkillsByCategory(category));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(skillService.searchSkills(keyword));
        }
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill details by ID")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all skills for a specific student")
    public ResponseEntity<List<Skill>> getSkillsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(skillService.getSkillsByStudentId(studentId));
    }

    @PostMapping
    @Operation(summary = "Add a skill to a student portfolio")
    public ResponseEntity<Skill> addSkill(@Valid @RequestBody SkillDto dto) {
        Skill created = skillService.addSkillToStudent(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update skill rating or proficiency")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillDto dto) {
        return ResponseEntity.ok(skillService.updateSkill(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
