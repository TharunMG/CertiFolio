package com.certifolio.service;

import com.certifolio.dto.SkillDto;
import com.certifolio.exception.ResourceNotFoundException;
import com.certifolio.model.Skill;
import com.certifolio.model.SkillCategory;
import com.certifolio.model.Student;
import com.certifolio.repository.SkillRepository;
import com.certifolio.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;

    public SkillService(SkillRepository skillRepository, StudentRepository studentRepository) {
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByStudentId(Long studentId) {
        return skillRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByCategory(SkillCategory category) {
        return skillRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Skill> searchSkills(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return skillRepository.findAll();
        }
        return skillRepository.searchSkillsByKeyword(keyword.trim());
    }

    public Skill addSkillToStudent(SkillDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));

        Skill skill = new Skill();
        skill.setName(dto.getName().trim());
        skill.setCategory(dto.getCategory());
        skill.setProficiencyLevel(dto.getProficiencyLevel());
        skill.setRating(dto.getRating());
        skill.setStudent(student);

        student.getSkills().add(skill);
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, SkillDto dto) {
        Skill skill = getSkillById(id);
        skill.setName(dto.getName().trim());
        skill.setCategory(dto.getCategory());
        skill.setProficiencyLevel(dto.getProficiencyLevel());
        skill.setRating(dto.getRating());
        return skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        Skill skill = getSkillById(id);
        skillRepository.delete(skill);
    }
}
