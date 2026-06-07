package com.elp.compliance_manager.virtualization.dto;

import com.elp.compliance_manager.virtualization.HostSummary;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class VirtualizationTopologyDTO {
    private Long companyId;
    private String companyName;
    private long totalVMs;
    private long totalHosts;
    private long totalClusters;
    private List<HostSummary> hostSummaries;
}