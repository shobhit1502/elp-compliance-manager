package com.elp.compliance_manager.virtualization;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class HostSummary {
    private String hostName;
    private String clusterName;
    private long vmCount;
    private int totalVCPU;
    private List<String> vmNames;
}