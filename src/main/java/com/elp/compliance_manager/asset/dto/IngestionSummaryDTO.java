package com.elp.compliance_manager.asset.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestionSummaryDTO {
    private int totalRows;
    private int savedCount;
    private int skippedCount;
    private String message;
}