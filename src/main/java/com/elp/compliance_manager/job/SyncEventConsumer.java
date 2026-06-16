package com.elp.compliance_manager.job;

import com.elp.compliance_manager.connector.ConnectorOrchestrator;
import com.elp.compliance_manager.connector.ConnectorSyncResult;
import com.elp.compliance_manager.connector.ConnectorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncEventConsumer {

    private final ConnectorOrchestrator orchestrator;
    private final SyncJobRepository syncJobRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "connector-sync-requests",
            groupId = "elp-compliance-group"
    )
    public void handleSyncRequest(String message) {
        log.info("Received sync request from Kafka: {}", message);

        SyncJob job = null;
        try {
            SyncRequestEvent event = objectMapper.readValue(
                    message, SyncRequestEvent.class);

            job = syncJobRepository.findById(event.getJobId())
                    .orElseThrow(() -> new RuntimeException(
                            "Job not found: " + event.getJobId()));

            job.setStatus(JobStatus.IN_PROGRESS);
            syncJobRepository.save(job);
            log.info("Job {} now IN_PROGRESS", event.getJobId());

            List<ConnectorSyncResult> results;
            if ("ALL".equals(event.getConnectorType())) {
                results = orchestrator.syncAll(
                        event.getCompanyId());
            } else {
                ConnectorType type = ConnectorType.valueOf(
                        event.getConnectorType());
                results = Arrays.asList(
                        orchestrator.sync(
                                event.getCompanyId(), type));
            }

            int totalAssets = results.stream()
                    .mapToInt(ConnectorSyncResult
                            ::getAssetsSaved).sum();
            int totalDeployments = results.stream()
                    .mapToInt(ConnectorSyncResult
                            ::getDeploymentsSaved).sum();

            job.setStatus(JobStatus.COMPLETED);
            job.setAssetsProcessed(results.stream()
                    .mapToInt(ConnectorSyncResult
                            ::getAssetsProcessed).sum());
            job.setAssetsSaved(totalAssets);
            job.setDeploymentsSaved(totalDeployments);
            job.setCompletedAt(LocalDateTime.now());
            syncJobRepository.save(job);

            log.info("Job {} COMPLETED. Assets: {}, " +
                            "Deployments: {}",
                    event.getJobId(), totalAssets,
                    totalDeployments);

        } catch (Exception e) {
            log.error("Job failed: {}", e.getMessage());
            if (job != null) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                job.setCompletedAt(LocalDateTime.now());
                syncJobRepository.save(job);
            }
        }
    }
}