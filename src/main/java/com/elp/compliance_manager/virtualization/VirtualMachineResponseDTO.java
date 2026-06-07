package com.elp.compliance_manager.virtualization.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VirtualMachineResponseDTO {
    private Long id;
    private String vmName;
    private String hostName;
    private String clusterName;
    private int vCPU;
    private int ramGb;
    private String operatingSystem;
    private String powerState;
    private boolean isInScope;
    private Long companyId;
    private LocalDateTime createdAt;
}