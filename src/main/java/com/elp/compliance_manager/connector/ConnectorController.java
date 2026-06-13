package com.elp.compliance_manager.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/connectors")
@RequiredArgsConstructor
public class ConnectorController {

    private final ConnectorOrchestrator orchestrator;

    @PostMapping("/sync/{companyId}/{connectorType}")
    public ResponseEntity<ConnectorSyncResult> sync(
            @PathVariable Long companyId,
            @PathVariable ConnectorType connectorType) {
        return ResponseEntity.ok(
                orchestrator.sync(companyId, connectorType));
    }

    @PostMapping("/sync-all/{companyId}")
    public ResponseEntity<List<ConnectorSyncResult>> syncAll(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(
                orchestrator.syncAll(companyId));
    }
}