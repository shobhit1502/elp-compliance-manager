package com.elp.compliance_manager.connector.impl;

import com.elp.compliance_manager.asset.AssetSource;
import com.elp.compliance_manager.asset.AssetType;
import com.elp.compliance_manager.connector.*;
import com.elp.compliance_manager.connector.mock.MockDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class VMwareConnector implements DataConnector {

    private final MockDataSource mockDataSource;
    private final AssetNormalizer normalizer;

    @Override
    public ConnectorType getType() {
        return ConnectorType.VMWARE_EXPORT;
    }

    @Override
    public List<NormalizedAsset> fetchData(Long companyId) {
        log.info("[VMwareConnector] Fetching data for company {}",
                companyId);

        List<Map<String, Object>> rawData =
                mockDataSource.getVMwareData();

        List<NormalizedAsset> normalized = new ArrayList<>();

        for (Map<String, Object> raw : rawData) {
            NormalizedAsset asset = NormalizedAsset.builder()
                    .machineName(str(raw.get("vm_name")))
                    .operatingSystem(
                            normalizer.normalizeOsName(
                                    str(raw.get("guest_os"))))
                    .assetType(AssetType.VIRTUAL_MACHINE)
                    .assetSource(AssetSource.VMWARE_EXPORT)
                    .installedSoftware(new ArrayList<>())
                    .build();
            normalized.add(asset);
        }

        log.info("[VMwareConnector] Fetched {} VM records",
                normalized.size());
        return normalized;
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }
}