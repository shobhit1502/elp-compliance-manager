package com.elp.compliance_manager.compliance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplianceResultRepository
        extends JpaRepository<ComplianceResult, Long> {

    List<ComplianceResult> findByCompanyId(Long companyId);

    Optional<ComplianceResult> findByCompanyIdAndProductId(
            Long companyId, Long productId);

    List<ComplianceResult> findByCompanyIdAndStatus(
            Long companyId, ComplianceStatus status);
}