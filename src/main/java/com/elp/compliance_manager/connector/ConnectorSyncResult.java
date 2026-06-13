package com.elp.compliance_manager.connector;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectorSyncResult {
    private ConnectorType connectorType;
    private int assetsProcessed;
    private int assetsSaved;
    private int assetsSkipped;
    private int deploymentsFound;
    private int deploymentsSaved;
    private String status;
    private String message;
}
