package com.college.certifolio.repository;

import com.college.certifolio.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByRollNumber(String rollNumber);
    Optional<StudentProfile> findByUserId(Long userId);
    boolean existsByRollNumber(String rollNumber);

    @Query("""
        SELECT DISTINCT sp FROM StudentProfile sp
        LEFT JOIN sp.studentSkills ss
        LEFT JOIN ss.skill s
        WHERE (:dept IS NULL OR :dept = '' OR LOWER(sp.department) = LOWER(:dept))
          AND (:minCgpa IS NULL OR sp.cgpa >= :minCgpa)
          AND (:skill IS NULL OR :skill = '' OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :skill, '%')))
        ORDER BY sp.cgpa DESC
    """)
    List<StudentProfile> searchStudents(
        @Param("skill") String skill,
        @Param("dept") String dept,
        @Param("minCgpa") Double minCgpa
    );
}
