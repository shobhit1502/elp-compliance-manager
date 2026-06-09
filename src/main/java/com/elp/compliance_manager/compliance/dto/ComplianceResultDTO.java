package com.elp.compliance_manager.compliance.dto;

import com.elp.compliance_manager.compliance.ComplianceStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ComplianceResultDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long productId;
    private String productName;
    private String productVersion;
    private String productEdition;
    private String productCategory;
    private int deployedQuantity;
    private int extrapolatedQuantity;
    private int licensedQuantity;
    private int gap;
    private ComplianceStatus status;
    private String notes;
    private LocalDateTime calculatedAt;
}