package com.college.certifolio.repository;

import com.college.certifolio.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByStudentProfileId(Long studentProfileId);
    Optional<StudentSkill> findByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);
    boolean existsByStudentProfileIdAndSkillId(Long studentProfileId, Long skillId);
}
