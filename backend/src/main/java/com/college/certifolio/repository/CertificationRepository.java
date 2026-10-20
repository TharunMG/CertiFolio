package com.college.certifolio.repository;

import com.college.certifolio.entity.Certification;
import com.college.certifolio.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByStudentProfileId(Long studentProfileId);
    
    // Faculty Queue: all pending certifications
    List<Certification> findByStatusOrderByCreatedAtDesc(VerificationStatus status);

    // Public Portfolio: strictly filter by VERIFIED status at the SQL layer
    List<Certification> findByStudentProfileRollNumberAndStatusOrderByIssueDateDesc(
        String rollNumber, VerificationStatus status
    );

    long countByStudentProfileIdAndStatus(Long studentProfileId, VerificationStatus status);
}
