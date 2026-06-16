package com.elp.compliance_manager.job;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class SyncJobController {

    private final SyncEventProducer producer;
    private final SyncJobRepository syncJobRepository;

    @PostMapping("/sync/{companyId}")
    public ResponseEntity<Map<String, String>> requestSync(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "ALL")
            String connectorType) {

        String jobId = producer.publishSyncRequest(
                companyId, connectorType);

        return ResponseEntity.accepted().body(Map.of(
                "jobId", jobId,
                "status", "QUEUED",
                "message", "Sync request queued. " +
                        "Poll /api/jobs/" + jobId +
                        " for status."
        ));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<SyncJob> getJobStatus(
            @PathVariable String jobId) {
        return syncJobRepository.findById(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SyncJob>> getJobsByCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                syncJobRepository
                        .findByCompanyIdOrderByRequestedAtDesc(
                                companyId));
    }
}