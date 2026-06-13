package com.elp.compliance_manager.connector;

import com.elp.compliance_manager.asset.AssetSource;
import com.elp.compliance_manager.asset.AssetType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class NormalizedAsset {
    private String machineName;
    private String operatingSystem;
    private AssetType assetType;
    private String domain;
    private String lastSeen;
    private String ipAddress;
    private String department;
    private AssetSource assetSource;
    private List<NormalizedSoftware> installedSoftware;
}