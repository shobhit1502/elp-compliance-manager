package com.elp.compliance_manager.coverage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageResponseDTO implements Serializable {

    private Long companyId;
    private String companyName;

    private long totalAssets;
    private long coveredAssets;
    private long uncoveredAssets;
    private double coveragePercentage;
    private double extrapolationFactor;

    private long totalWorkstations;
    private long coveredWorkstations;
    private long totalServers;
    private long coveredServers;

    private String coverageStatus;
}