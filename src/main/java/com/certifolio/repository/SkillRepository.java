package com.certifolio.repository;

import com.certifolio.model.Skill;
import com.certifolio.model.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByStudentId(Long studentId);

    List<Skill> findByCategory(SkillCategory category);

    @Query("SELECT s.name, COUNT(s) as cnt FROM Skill s GROUP BY s.name ORDER BY cnt DESC")
    List<Object[]> findTopSkillsRaw();

    @Query("SELECT s.category, COUNT(s) FROM Skill s GROUP BY s.category")
    List<Object[]> countSkillsByCategoryRaw();

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Skill> searchSkillsByKeyword(@Param("keyword") String keyword);
}
