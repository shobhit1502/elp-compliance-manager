package com.elp.compliance_manager.asset.dto;

import com.elp.compliance_manager.asset.AssetSource;
import com.elp.compliance_manager.asset.AssetType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AssetResponseDTO {
    private Long id;
    private String machineName;
    private String ipAddress;
    private String operatingSystem;
    private String osVersion;
    private AssetType assetType;
    private AssetSource assetSource;
    private String domain;
    private String lastSeen;
    private boolean isInScope;
    private boolean hasScriptOutput;
    private Long companyId;
    private LocalDateTime createdAt;
}