package com.certifolio.repository;

import com.certifolio.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegNo(String regNo);

    Optional<Student> findByEmail(String email);

    boolean existsByRegNo(String regNo);

    boolean existsByEmail(String email);

    List<Student> findByDepartmentIgnoreCase(String department);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.regNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);

    @Query("SELECT DISTINCT s FROM Student s JOIN s.skills sk WHERE LOWER(sk.name) LIKE LOWER(CONCAT('%', :skillName, '%'))")
    List<Student> findStudentsBySkillName(@Param("skillName") String skillName);

    @Query("SELECT DISTINCT s.department FROM Student s ORDER BY s.department ASC")
    List<String> findAllDepartments();
}
