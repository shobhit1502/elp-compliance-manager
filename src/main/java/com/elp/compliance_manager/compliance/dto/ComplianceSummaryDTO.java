package com.elp.compliance_manager.compliance.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ComplianceSummaryDTO {
    private Long companyId;
    private String companyName;
    private int totalProductsAnalyzed;
    private int compliantProducts;
    private int underLicensedProducts;
    private int overLicensedProducts;
    private double coveragePercentage;
    private double extrapolationFactor;
    private List<ComplianceResultDTO> results;
}