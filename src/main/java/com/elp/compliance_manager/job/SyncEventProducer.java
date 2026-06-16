package com.elp.compliance_manager.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SyncJobRepository syncJobRepository;

    public String publishSyncRequest(Long companyId,
                                     String connectorType) {
        String jobId = UUID.randomUUID().toString();

        try {
            SyncRequestEvent event = SyncRequestEvent.builder()
                    .jobId(jobId)
                    .companyId(companyId)
                    .connectorType(connectorType)
                    .requestedAt(LocalDateTime.now().toString())
                    .build();

            String message = objectMapper
                    .writeValueAsString(event);

            SyncJob job = SyncJob.builder()
                    .jobId(jobId)
                    .companyId(companyId)
                    .connectorType(connectorType)
                    .status(JobStatus.QUEUED)
                    .requestedAt(LocalDateTime.now())
                    .build();
            syncJobRepository.save(job);

            kafkaTemplate.send(
                    "connector-sync-requests",
                    companyId.toString(),
                    message);

            log.info("Published sync request. JobId: {}, " +
                            "Company: {}, Type: {}",
                    jobId, companyId, connectorType);

            return jobId;

        } catch (Exception e) {
            log.error("Failed to publish sync request: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Failed to publish sync request", e);
        }
    }
}