package com.elp.compliance_manager.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AuditLog>> getLogsByCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                auditService.getLogsByCompany(companyId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        return ResponseEntity.ok(auditService.getRecentLogs());
    }
}