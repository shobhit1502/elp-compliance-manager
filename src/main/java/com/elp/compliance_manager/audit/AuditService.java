package com.elp.compliance_manager.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityType,
                    String entityId, String performedBy,
                    Long companyId, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .companyId(companyId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByCompany(Long companyId) {
        return auditLogRepository
                .findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }
}