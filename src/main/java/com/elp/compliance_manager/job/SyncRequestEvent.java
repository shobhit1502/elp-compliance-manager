package com.elp.compliance_manager.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequestEvent implements Serializable {
    private String jobId;
    private Long companyId;
    private String connectorType;
    private String requestedAt;
}