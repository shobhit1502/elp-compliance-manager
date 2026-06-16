package com.elp.compliance_manager.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncJob {

    @Id
    private String jobId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "connector_type")
    private String connectorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    @Column(name = "assets_processed")
    private Integer assetsProcessed;

    @Column(name = "assets_saved")
    private Integer assetsSaved;

    @Column(name = "deployments_saved")
    private Integer deploymentsSaved;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}