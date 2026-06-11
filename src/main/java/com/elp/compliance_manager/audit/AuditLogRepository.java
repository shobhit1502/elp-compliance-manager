package com.elp.compliance_manager.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    List<AuditLog> findByPerformedByOrderByCreatedAtDesc(
            String performedBy);
    List<AuditLog> findTop50ByOrderByCreatedAtDesc();
}