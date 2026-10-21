package com.certifolio.repository;

import com.certifolio.model.Certification;
import com.certifolio.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByStudentId(Long studentId);

    List<Certification> findByVerificationStatusOrderByIssueDateDesc(VerificationStatus status);

    long countByVerificationStatus(VerificationStatus status);

    @Query("SELECT c.issuingOrganization, COUNT(c) FROM Certification c GROUP BY c.issuingOrganization ORDER BY COUNT(c) DESC")
    List<Object[]> countCertificationsByIssuerRaw();

    @Query("SELECT c FROM Certification c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.issuingOrganization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.credentialId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Certification> searchCertifications(@Param("keyword") String keyword);
}
