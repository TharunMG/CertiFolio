package com.college.certifolio.repository;

import com.college.certifolio.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findBySkillNameIgnoreCase(String skillName);
    boolean existsBySkillNameIgnoreCase(String skillName);
}
